package com.Order.order_service.DTOs.Order;

import com.Order.order_service.DTOs.OrderItem.OrderItemResponse;
import com.Order.order_service.DTOs.ShippingAddress.ShippingAddressDTO;
import com.Order.order_service.Enum.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        Long userId,
        LocalDateTime estimatedDeliveryDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        OrderStatus status,
        BigDecimal totalAmount,
        String fullName,
        String phoneNumber,
        List<OrderItemResponse> orderItems,
        ShippingAddressDTO shippingAddress
) {}
