package com.Order.orderservice.Services;

import com.Order.orderservice.DTOs.Cart.CartResponse;
import com.Order.orderservice.DTOs.CartItem.CartItemRequest;
import org.springframework.stereotype.Service;

@Service
public interface CartService {
    CartResponse createCart(Long userId);
    CartResponse addToCart(Long userId, CartItemRequest request);
    void clearCartByUserId(Long userId);
    void delete(Long userId);
    CartResponse getCartByUserId(Long userId);
    CartResponse removeFromCart(Long userId,Long cartItemId);

}
