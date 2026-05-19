package com.Order.orderservice.ServiceImpl;

import com.Order.orderservice.DTOs.Order.OrderRequest;
import com.Order.orderservice.DTOs.Order.OrderResponse;
import com.Order.orderservice.DTOs.OrderItem.OrderItemRequest;
import com.Order.orderservice.DTOs.OrderItem.OrderItemResponse;
import com.Order.orderservice.DTOs.PageInfo;
import com.Order.orderservice.DTOs.Product.ProductResponse;
import com.Order.orderservice.DTOs.ReservationDTO.ReservationResponse;
import com.Order.orderservice.DTOs.ShippingAddress.ShippingAddressDTO;
import com.Order.orderservice.Entities.Order;
import com.Order.orderservice.Entities.OrderItem;
import com.Order.orderservice.Entities.ShippingAddress;
import com.Order.orderservice.Enum.OrderStatus;
import com.Order.orderservice.Mapper.OrderItemMapper;
import com.Order.orderservice.Mapper.OrderMapper;
import com.Order.orderservice.Mapper.ShippingAddressMapper;
import com.Order.orderservice.Repository.OrderRepository;
import com.Order.orderservice.Services.OrderService;
import com.Order.orderservice.client.InventoryClient;
import com.Order.orderservice.client.ProductClient;
import com.shared_library.Exceptions.BusinessInvalidException;
import com.shared_library.Exceptions.RateLimitExceededException;
import com.shared_library.Exceptions.ResourceNotFoundException;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ShippingAddressMapper shippingAddressMapper;
    private final ProductClient productClient;
    private final InventoryClient inventoryClient;

    @Override
    @Transactional
    @RateLimiter(name = "createOrder",fallbackMethod = "createOrderRateLimiterFallback")
    public OrderResponse createOrder(Long userId, OrderRequest request) {
        if (orderRepository.existsByUserIdAndStatus(userId, OrderStatus.DRAFT)) {
            throw new BusinessInvalidException(
                    "You already have a draft order. Please confirm or cancel it first."
            );
        }

        Order order = Order.builder()
                .userId(userId)
                .fullName(request.fullName())
                .phoneNumber(request.phoneNumber())
                .status(OrderStatus.DRAFT)
                .estimatedDeliveryDate(LocalDateTime.now().plusDays(8))
                .totalAmount(BigDecimal.ZERO)
                .build();

        List<Long> productIds = request.orderItems().stream()
                .map(OrderItemRequest::productId)
                .toList();

        List<ReservationResponse> reservationResponses = inventoryClient.validateActiveReservation(productIds);

        // this verifies the quantity of orderItemRequest in orderRequest and Reservation using productId
        verifyQuantity(reservationResponses, request.orderItems());

        List<ProductResponse> productResponses = productClient.getByIds(productIds);

        Map<Long, ProductResponse> productMap = productResponses.stream()
                .collect(Collectors.toMap(
                        ProductResponse::id, p -> p
                ));


        request.orderItems().forEach(itemRequest -> {
            ProductResponse product = productMap.get(itemRequest.productId());
            OrderItem item = OrderItem.builder()
                    .productId(product.id())
                    .quantity(itemRequest.quantity())
                    .priceAtPurchase(product.price())
                    .discountAtPurchase(product.discount() != null
                            ? product.discount() : BigDecimal.ZERO)
                    .build();
            item.calculateAndSetSubTotal();
            order.addOrderItem(item);
        });

        order.calculateTotalAmount();

        ShippingAddress shippingAddress = shippingAddressMapper.toShippingAddress(request.shippingAddress());
        order.setShippingAddress(shippingAddress);

        Order saved = this.orderRepository.save(order);
        log.info("Order created:{} for userId: {}", saved.getId(), userId);


        List<OrderItemResponse> orderItemResponses = this.orderItemMapper.toOrderItemResponses(saved.getOrderItems());
        ShippingAddressDTO shippingAddressDTO = this.shippingAddressMapper.toDTO(saved.getShippingAddress());
        return this.orderMapper.toResponse(
                saved,
                orderItemResponses,
                shippingAddressDTO

        );
    }

    @Override
    @Transactional(readOnly = true)
    @RateLimiter(name = "orderRead",fallbackMethod = "getOrderRateLimiterFallback")
    public OrderResponse getOrderById(Long orderId, Long userId) {
        Order order = this.orderRepository.findByIdAndUserIdWithDraftStatus(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        List<OrderItemResponse> orderItemResponses = this.orderItemMapper.toOrderItemResponses(order.getOrderItems());
        ShippingAddressDTO addressDTO = this.shippingAddressMapper.toDTO(order.getShippingAddress());
        return this.orderMapper.toResponse(
                order,
                orderItemResponses,
                addressDTO
        );
    }


    @Override
    @Transactional
    @RateLimiter(name = "createOrder",fallbackMethod = "cancelOrderRateLimitFallback")
    public void cancelOrder(Long orderId, Long userId) {
        Order order = this.orderRepository.findByIdAndUserIdWithDraftStatus(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found."));

        List<Long> productIds = order.getOrderItems().stream()
                .map(OrderItem::getProductId)
                .toList();
        if (!productIds.isEmpty()) {
            inventoryClient.releaseAllReservation(productIds);
        }

        this.orderRepository.delete(order);
        log.info("Order cancelled: {} for userId: {}", orderId, userId);
    }

    @Override
    public PageInfo<OrderResponse> getOrdersByUserId(Long userId, Integer pageNumber, Integer pageSize) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    @Transactional
    @RateLimiter(name = "confirmOrder",fallbackMethod = "confirmOrderRateLimiterFallback")
    public OrderResponse confirmOrder(Long orderId, Long userId) {
        Order order = this.orderRepository.findByIdAndUserIdWithDraftStatus(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Order with status DRAFT not found..."));

        // Step 1: Validate inventory BEFORE changing order status
        List<Long> productIds = order.getOrderItems().stream()
                .map(OrderItem::getProductId).toList();

        try {
            // This should FAIL if inventory is insufficient or service is down
            inventoryClient.convertReservations(productIds);
        }catch (BusinessInvalidException e){
            log.error("Business error converting reservations for orderId: {}. Cause: {}",
                    orderId, e.getMessage());
            throw e;
        }
        catch (Exception e) {
            // Order remains DRAFT - safe to retry
            log.error("Failed to convert reservations, keeping order in DRAFT: {}", orderId);
            throw new BusinessInvalidException("Order confirmation failed. Please try again.");
        }

        // Step 2: Only update order status AFTER inventory is successfully deducted
        order.setStatus(OrderStatus.CONFIRMED);
        Order saved = this.orderRepository.save(order);

        // Step 3: Build response and return
        List<OrderItemResponse> orderItemResponses = this.orderItemMapper.toOrderItemResponses(saved.getOrderItems());
        ShippingAddressDTO shippingAddressDTO = this.shippingAddressMapper.toDTO(saved.getShippingAddress());
        return this.orderMapper.toResponse(saved, orderItemResponses, shippingAddressDTO);
    }

    @Override
    @Transactional
    public OrderResponse shipOrder(Long orderId) {
        Order order = this.orderRepository
                .findByIdAndUserIdWithConfirmedStatus(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Not Found Order with confirmed status"));

        order.setStatus(OrderStatus.SHIPPED);
        Order saved = this.orderRepository.save(order);
        List<OrderItemResponse> orderItemResponses = this.orderItemMapper.toOrderItemResponses(saved.getOrderItems());
        ShippingAddressDTO shippingAddressDTO = this.shippingAddressMapper.toDTO(saved.getShippingAddress());
        return this.orderMapper.toResponse(
                saved,
                orderItemResponses,
                shippingAddressDTO

        );
    }

    @Override
    @Transactional
    public OrderResponse deliverOrder(Long orderId) {
        Order order = this.orderRepository
                .findByIdAndUserIdWithShippedStatus(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Not Found Order with confirmed status"));

        order.setStatus(OrderStatus.DELIVERED);
        Order saved = this.orderRepository.save(order);
        List<OrderItemResponse> orderItemResponses = this.orderItemMapper.toOrderItemResponses(saved.getOrderItems());
        ShippingAddressDTO shippingAddressDTO = this.shippingAddressMapper.toDTO(saved.getShippingAddress());
        return this.orderMapper.toResponse(
                saved,
                orderItemResponses,
                shippingAddressDTO

        );
    }


    //  =========== HELPER METHODS =============
    void verifyQuantity(List<ReservationResponse> reservations, List<OrderItemRequest> orderItems) {
        // build a map of productId → reservedQuantity from reservations
        Map<Long, Long> reservationMap = reservations.stream()
                .collect(Collectors.toMap(
                        ReservationResponse::productId,
                        ReservationResponse::reservedQuantity
                ));

        orderItems.forEach(item -> {
            Long reservedQuantity = reservationMap.get(item.productId());

            // reservation not found for this product
            if (reservedQuantity == null) {
                throw new BusinessInvalidException(
                        "No active reservation found for product: " + item.productId()
                                + ". Please add to cart again."
                );
            }

            // quantity mismatch
            if (!reservedQuantity.equals(item.quantity().longValue())) {
                throw new BusinessInvalidException(
                        String.format(
                                "Quantity mismatch for product %d. Reserved: %d, Ordered: %d. " +
                                        "Please update your cart.",
                                item.productId(),
                                reservedQuantity,
                                item.quantity()
                        )
                );
            }
        });
    }


    // =========== FALLBACK METHOD ==============

    public OrderResponse createOrderRateLimiterFallback(Long userId, OrderRequest request, RequestNotPermitted e){
        log.warn("Rate limiter exceeded for createOrder.userId:{}",userId);

        throw new RateLimitExceededException(
                "Too many order requests.Please wait a moment and try again."
        );
    }

    public OrderResponse confirmOrderRateLimitFallback(
            Long orderId,
            Long userId,
            RequestNotPermitted e) {
        log.warn("Rate limit exceeded for confirmOrder. orderId: {}", orderId);
        throw new RateLimitExceededException(
                "Too many requests. Please wait a moment and try again."
        );
    }



    public OrderResponse getOrderRateLimitFallback(
            Long orderId,
            Long userId,
            RequestNotPermitted e) {
        log.warn("Rate limit exceeded for getOrderById. orderId: {}", orderId);
        throw new RateLimitExceededException(
                "Too many requests. Please slow down."
        );
    }

    public void cancelOrderRateLimitFallback(
            Long orderId,
            Long userId,
            RequestNotPermitted e) {
        log.warn("Rate limit exceeded for cancelOrder. orderId: {}", orderId);
        throw new RateLimitExceededException(
                "Too many requests. Please wait a moment and try again."
        );
    }



}
