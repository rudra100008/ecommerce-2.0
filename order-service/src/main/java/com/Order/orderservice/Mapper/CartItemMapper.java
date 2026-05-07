package com.Order.orderservice.Mapper;

import com.Order.orderservice.DTOs.CartItem.CartItemRequest;
import com.Order.orderservice.DTOs.CartItem.CartItemResponse;
import com.Order.orderservice.Entities.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CartItemMapper {

    @Mapping(target = "id",ignore = true)
    @Mapping(target = "reservationId",ignore = true)
    @Mapping(target = "priceAtAddTime",ignore = true)
    @Mapping(target = "discountAtAddTime",ignore = true)
    @Mapping(target = "cart",ignore = true)
    CartItem toCartItem(CartItemRequest  request);

    CartItemResponse toResponse(CartItem cartItem);

    default List<CartItemResponse> toCartItemResponseList(List<CartItem> cartItems){
        if (cartItems == null){
            return  new ArrayList<>();
        }

        return cartItems.stream()
                .map(this::toResponse)
                .toList();
    }
}
