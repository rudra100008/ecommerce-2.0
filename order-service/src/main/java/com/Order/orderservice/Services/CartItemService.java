package com.Order.orderservice.Services;


import com.Order.orderservice.DTOs.CartItem.CartItemRequest;
import com.Order.orderservice.DTOs.CartItem.CartItemResponse;
import com.Order.orderservice.Entities.CartItem;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CartItemService {

    List<CartItem> save(List<CartItemRequest> cartItemRequests);
    CartItemResponse updateQuantity(Long id,Integer quantity);
    void delete(Long id,Long cartId);

}
