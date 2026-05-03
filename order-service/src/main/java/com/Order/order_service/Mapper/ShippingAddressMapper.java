package com.Order.order_service.Mapper;

import com.Order.order_service.DTOs.ShippingAddress.ShippingAddressDTO;
import com.Order.order_service.Entities.ShippingAddress;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ShippingAddressMapper {

    ShippingAddress toShippingAddress(ShippingAddressDTO dto);

    ShippingAddressDTO toDTO(ShippingAddress shippingAddress);
}
