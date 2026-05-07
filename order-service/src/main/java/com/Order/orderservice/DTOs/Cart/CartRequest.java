package com.Order.orderservice.DTOs.Cart;

import com.Order.orderservice.DTOs.CartItem.CartItemRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CartRequest(
        @NotNull(message = "User ID is required.")
        Long userId,
        @Valid
        @NotEmpty(message = "Cart should have at least one item")
        List<CartItemRequest> cartItems
) {
}
