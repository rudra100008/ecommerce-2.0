package com.Order.orderservice.Mapper;

import com.Order.orderservice.DTOs.OrderItem.OrderItemRequest;
import com.Order.orderservice.DTOs.OrderItem.OrderItemResponse;
import com.Order.orderservice.Entities.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(target = "order", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "priceAtPurchase", ignore = true)    // ← set manually in service
    @Mapping(target = "discountAtPurchase", ignore = true) // ← set manually in service
    @Mapping(target = "subTotal", ignore = true)
    OrderItem toOrderItem(OrderItemRequest request);

    OrderItemResponse toResponse(OrderItem orderItem);


    default List<OrderItemResponse> toOrderItemResponses(List<OrderItem> orderItems){
        if(orderItems == null){
            return new ArrayList<>();
        }
        return orderItems.stream()
                .map(this::toResponse)
                .toList();
    }
}
