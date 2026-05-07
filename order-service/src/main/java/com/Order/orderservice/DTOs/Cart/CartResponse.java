package com.Order.orderservice.DTOs.Cart;

import com.Order.orderservice.DTOs.CartItem.CartItemResponse;

import java.util.List;

public record CartResponse(
        Long id,
        Long userId,
        List<CartItemResponse> cartItems
) {

}
