package com.Order.order_service.Services;


import com.Order.order_service.DTOs.CartItem.CartItemRequest;
import com.Order.order_service.DTOs.CartItem.CartItemResponse;
import com.Order.order_service.Entities.CartItem;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CartItemService {

    List<CartItem> save(List<CartItemRequest> cartItemRequests);
    CartItemResponse updateQuantity(Long id,Integer quantity);
    void delete(Long id,Long cartId);

}
