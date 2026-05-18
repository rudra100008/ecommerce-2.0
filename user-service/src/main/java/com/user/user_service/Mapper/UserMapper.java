package com.user.user_service.Mapper;

import com.user.user_service.DTOs.Address.AddressResponse;
import com.user.user_service.DTOs.User.UserResponse;
import com.user.user_service.Entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "user.id",target = "userId")
    @Mapping(source = "addresses",target = "addresses")
    UserResponse toResponse(User user , List<AddressResponse> addresses);
}
