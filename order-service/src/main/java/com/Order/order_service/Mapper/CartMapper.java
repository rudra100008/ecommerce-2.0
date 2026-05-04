package com.Order.order_service.Mapper;

import com.Order.order_service.DTOs.Cart.CartRequest;
import com.Order.order_service.DTOs.Cart.CartResponse;
import com.Order.order_service.DTOs.CartItem.CartItemResponse;
import com.Order.order_service.Entities.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(target = "id",ignore = true)
    @Mapping(target = "cartItems",ignore = true)
    @Mapping(target = "createdAt",ignore = true)
    @Mapping(target = "updatedAt",ignore = true)
    Cart toCart(CartRequest cartRequest);

    @Mapping(source = "cartItems",target = "cartItems")
    CartResponse toResponse(Cart cart, List<CartItemResponse> cartItems);
}
