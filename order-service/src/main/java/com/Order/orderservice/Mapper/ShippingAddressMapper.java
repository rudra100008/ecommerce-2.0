package com.Order.orderservice.Mapper;

import com.Order.orderservice.DTOs.ShippingAddress.ShippingAddressDTO;
import com.Order.orderservice.Entities.ShippingAddress;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ShippingAddressMapper {

    ShippingAddress toShippingAddress(ShippingAddressDTO dto);

    ShippingAddressDTO toDTO(ShippingAddress shippingAddress);
}
