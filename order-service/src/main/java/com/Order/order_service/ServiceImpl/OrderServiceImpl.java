package com.Order.order_service.ServiceImpl;

import com.Order.order_service.DTOs.Order.OrderRequest;
import com.Order.order_service.DTOs.Order.OrderResponse;
import com.Order.order_service.DTOs.Order.UpdateOrderRequest;
import com.Order.order_service.DTOs.OrderItem.OrderItemResponse;
import com.Order.order_service.DTOs.PageInfo;
import com.Order.order_service.DTOs.Product.ProductResponse;
import com.Order.order_service.DTOs.ShippingAddress.ShippingAddressDTO;
import com.Order.order_service.Entities.Order;
import com.Order.order_service.Entities.OrderItem;
import com.Order.order_service.Entities.ShippingAddress;
import com.Order.order_service.Enum.OrderStatus;
import com.Order.order_service.Exceptions.BusinessInvalidException;
import com.Order.order_service.Exceptions.ResourceNotFoundException;
import com.Order.order_service.Mapper.OrderItemMapper;
import com.Order.order_service.Mapper.OrderMapper;
import com.Order.order_service.Mapper.ShippingAddressMapper;
import com.Order.order_service.Repository.OrderRepository;
import com.Order.order_service.Services.OrderItemService;
import com.Order.order_service.Services.OrderService;
import com.Order.order_service.Services.ProductClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ShippingAddressMapper shippingAddressMapper;
    private final ProductClient productClient;

    @Override
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
        Order order = this.orderRepository.findByIdAndUserId(orderId,userId)
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
        Order order = this.orderRepository.findByIdAndUserId(orderId,userId)
                .orElseThrow(()-> new ResourceNotFoundException("Order not found."));

        order.getOrderItems().forEach(order::removeOrderItem);

        this.orderRepository.delete(order);
    }

    @Override
    public PageInfo<OrderResponse> getOrdersByUserId(Long userId, Integer pageNumber, Integer pageSize) {
        return null;
    }

    @Override
    public OrderResponse confirmOrder(Long orderId, Long userId) {
        return null;
    }

    @Override
    public OrderResponse shipOrder(Long orderId) {
        return null;
    }

    @Override
    public OrderResponse deliverOrder(Long orderId) {
        return null;
    }


}
