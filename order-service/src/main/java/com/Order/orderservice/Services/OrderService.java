package com.Order.orderservice.Services;

import com.Order.orderservice.DTOs.Order.OrderRequest;
import com.Order.orderservice.DTOs.Order.OrderResponse;
import com.Order.orderservice.DTOs.PageInfo;
import org.springframework.stereotype.Service;

@Service
public interface OrderService {
    OrderResponse createOrder(Long userId, OrderRequest request);
    OrderResponse getOrderById(Long orderId, Long userId);
    void cancelOrder(Long orderId, Long userId);
    PageInfo<OrderResponse> getOrdersByUserId(Long userId, Integer pageNumber, Integer pageSize);


    OrderResponse confirmOrder(Long orderId, Long userId);   // DRAFT → CONFIRMED
    OrderResponse shipOrder(Long orderId);                   // CONFIRMED → SHIPPED   (admin)
    OrderResponse deliverOrder(Long orderId);

}
