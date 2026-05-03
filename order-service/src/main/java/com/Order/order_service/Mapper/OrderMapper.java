package com.Order.order_service.Mapper;

import com.Order.order_service.DTOs.Order.OrderRequest;
import com.Order.order_service.DTOs.Order.OrderResponse;
import com.Order.order_service.DTOs.OrderItem.OrderItemResponse;
import com.Order.order_service.DTOs.ShippingAddress.ShippingAddressDTO;
import com.Order.order_service.Entities.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "totalAmount", ignore = true)
    @Mapping(target = "estimatedDeliveryDate", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    @Mapping(target = "shippingAddress", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Order toOrder(OrderRequest request);


    @Mapping(target = "orderItems", source = "orderItems")
    @Mapping(target = "shippingAddress", source = "shippingAddress")
    OrderResponse toResponse(Order order, List<OrderItemResponse> orderItems, ShippingAddressDTO shippingAddress);
}
