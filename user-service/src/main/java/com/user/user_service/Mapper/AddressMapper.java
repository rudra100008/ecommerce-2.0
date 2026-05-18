package com.user.user_service.Mapper;

import com.user.user_service.DTOs.Address.AddressResponse;
import com.user.user_service.Entities.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    @Mapping(source = "id",target = "addressId")
    AddressResponse toResponse(Address address);

    default List<AddressResponse> toAddressResponseList(List<Address> addresses){
        if(addresses == null || addresses.isEmpty()){
            return new ArrayList<>();
        }
        return addresses.stream()
                .map(this::toResponse)
                .toList();
    }
}
