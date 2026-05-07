package com.Order.orderservice.DTOs.Order;

import com.Order.orderservice.DTOs.OrderItem.OrderItemRequest;
import com.Order.orderservice.DTOs.ShippingAddress.ShippingAddressDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public record OrderRequest(
        @NotNull(message = "User ID is required")
        Long userId,
        @NotBlank(message = "Full name is required")
        String fullName,

        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "^9\\d{9}$", message = "Phone number must start with 9 and have 10 digits")
        String phoneNumber,

        @NotEmpty(message = "Order must have at least one item")
        @Valid
        List<OrderItemRequest> orderItems,

        @NotNull(message = "Shipping address is required")
        @Valid
        ShippingAddressDTO shippingAddress
) {
}
