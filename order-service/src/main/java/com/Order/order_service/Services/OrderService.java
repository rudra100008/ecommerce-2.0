package com.Order.order_service.Services;

import com.Order.order_service.DTOs.Order.OrderRequest;
import com.Order.order_service.DTOs.Order.OrderResponse;
import com.Order.order_service.DTOs.Order.UpdateOrderRequest;
import com.Order.order_service.DTOs.PageInfo;
import org.springframework.stereotype.Service;

@Service
public interface OrderService {
    OrderResponse createOrder(OrderRequest request);
    OrderResponse getOrderById(Long orderId, Long userId);
    OrderResponse updateOrder(Long orderId, Long userId, UpdateOrderRequest request);
    void cancelOrder(Long orderId, Long userId);
    PageInfo<OrderResponse> getOrdersByUserId(Long userId, Integer pageNumber, Integer pageSize);


    OrderResponse confirmOrder(Long orderId, Long userId);   // DRAFT → CONFIRMED
    OrderResponse shipOrder(Long orderId);                   // CONFIRMED → SHIPPED   (admin)
    OrderResponse deliverOrder(Long orderId);

}
