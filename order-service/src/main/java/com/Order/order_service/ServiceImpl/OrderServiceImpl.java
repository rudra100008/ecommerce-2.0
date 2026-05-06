package com.Order.order_service.ServiceImpl;

import com.Order.order_service.DTOs.Order.OrderRequest;
import com.Order.order_service.DTOs.Order.OrderResponse;
import com.Order.order_service.DTOs.OrderItem.OrderItemRequest;
import com.Order.order_service.DTOs.OrderItem.OrderItemResponse;
import com.Order.order_service.DTOs.PageInfo;
import com.Order.order_service.DTOs.Product.ProductResponse;
import com.Order.order_service.DTOs.ReservationDTO.ReservationResponse;
import com.Order.order_service.DTOs.ShippingAddress.ShippingAddressDTO;
import com.Order.order_service.Entities.Order;
import com.Order.order_service.Entities.OrderItem;
import com.Order.order_service.Entities.ShippingAddress;
import com.Order.order_service.Enum.OrderStatus;
import com.Order.order_service.Mapper.OrderItemMapper;
import com.Order.order_service.Mapper.OrderMapper;
import com.Order.order_service.Mapper.ShippingAddressMapper;
import com.Order.order_service.Repository.OrderRepository;
import com.Order.order_service.Services.OrderService;
import com.Order.order_service.client.InventoryClient;
import com.Order.order_service.client.ProductClient;
import com.shared_library.Exceptions.BusinessInvalidException;
import com.shared_library.Exceptions.ResourceNotFoundException;
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
    public OrderResponse createOrder(OrderRequest request) {
        if(orderRepository.existsByUserIdAndStatus(request.userId(), OrderStatus.DRAFT)){
            throw new BusinessInvalidException(
                    "You already have a draft order. Please confirm or cancel it first."
            );
        }

        Order order = Order.builder()
                .userId(request.userId())
                .fullName(request.fullName())
                .phoneNumber(request.phoneNumber())
                .status(OrderStatus.DRAFT)
                .estimatedDeliveryDate(LocalDateTime.now().plusDays(8))
                .totalAmount(BigDecimal.ZERO)
                .build();

        List<Long> productIds = request.orderItems().stream()
                .map(o -> o.productId())
                        .toList();

        List<ReservationResponse> reservationResponses = inventoryClient.validateActiveReservation(request.userId(),productIds);

        // this verifies the quantity of orderItemRequest in orderRequest and Reservation using productId
        verifyQuantity(reservationResponses,request.orderItems());


        request.orderItems().forEach(itemRequest ->{
            ProductResponse product  = this.productClient.getById(itemRequest.productId());
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
        log.info("Order created:{} for userId: {}",saved.getId(),request.userId());


        List<OrderItemResponse> orderItemResponses = this.orderItemMapper.toOrderItemResponses(saved.getOrderItems());
        ShippingAddressDTO shippingAddressDTO = this.shippingAddressMapper.toDTO(saved.getShippingAddress());
        return this.orderMapper.toResponse(
                saved,
                orderItemResponses,
                shippingAddressDTO

        );
    }

    @Override
    public OrderResponse getOrderById(Long orderId, Long userId) {
        Order order = this.orderRepository.findDraftByIdAndUserId(orderId,userId)
                .orElseThrow(()-> new ResourceNotFoundException("Order not found"));

        List<OrderItemResponse> orderItemResponses = this.orderItemMapper.toOrderItemResponses(order.getOrderItems());
        ShippingAddressDTO addressDTO = this.shippingAddressMapper.toDTO(order.getShippingAddress());
        return  this.orderMapper.toResponse(
                order,
                orderItemResponses,
                addressDTO
        );
    }


    @Override
    public void cancelOrder(Long orderId, Long userId) {
        Order order = this.orderRepository.findDraftByIdAndUserId(orderId,userId)
                .orElseThrow(()-> new ResourceNotFoundException("Order not found."));

        List<Long> productIds = order.getOrderItems().stream()
                .map(OrderItem::getProductId)
                .toList();
        if (!productIds.isEmpty()){
            inventoryClient.releaseAllReservation(userId,productIds);
        }

        this.orderRepository.delete(order);
        log.info("Order cancelled: {} for userId: {}", orderId, userId);
    }

    @Override
    public PageInfo<OrderResponse> getOrdersByUserId(Long userId, Integer pageNumber, Integer pageSize) {
        return null;
    }

    @Override
    @Transactional
    public OrderResponse confirmOrder(Long orderId, Long userId) {
        Order order = this.orderRepository.findDraftByIdAndUserId(orderId,userId)
                .orElseThrow(()->new ResourceNotFoundException("Order with status DRAFT not found. Retry again."));

        List<Long> productIds = order.getOrderItems()
                .stream()
                .map(OrderItem::getProductId)
                .toList();

        if (!productIds.isEmpty()) {
            inventoryClient.convertReservations(userId, productIds);
        }
        order.setStatus(OrderStatus.CONFIRMED);

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
    public OrderResponse shipOrder(Long orderId) {
        return null;
    }

    @Override
    public OrderResponse deliverOrder(Long orderId) {
        return null;
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

}
