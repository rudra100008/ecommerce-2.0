package com.Order.order_service.Services;

import com.Order.order_service.DTOs.Cart.CartRequest;
import com.Order.order_service.DTOs.Cart.CartResponse;
import org.springframework.stereotype.Service;

@Service
public interface CartService {
    CartResponse addToCart(CartRequest request);
}
