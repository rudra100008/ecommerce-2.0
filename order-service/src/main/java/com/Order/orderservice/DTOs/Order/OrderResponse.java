package com.Order.orderservice.DTOs.Order;

import com.Order.orderservice.DTOs.OrderItem.OrderItemResponse;
import com.Order.orderservice.DTOs.ShippingAddress.ShippingAddressDTO;
import com.Order.orderservice.Enum.OrderStatus;

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
