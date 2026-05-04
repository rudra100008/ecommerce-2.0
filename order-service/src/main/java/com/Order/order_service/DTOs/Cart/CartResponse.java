package com.Order.order_service.DTOs.Cart;

import com.Order.order_service.DTOs.CartItem.CartItemResponse;

import java.util.List;

public record CartResponse(
        Long id,
        Long userId,
        List<CartItemResponse> cartItems
) {

}
