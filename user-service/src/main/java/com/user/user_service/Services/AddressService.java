package com.user.user_service.Services;

import com.user.user_service.DTOs.Address.AddressRequest;
import com.user.user_service.DTOs.Address.AddressResponse;
import com.user.user_service.DTOs.Address.UpdateAddressRequest;
import com.user.user_service.DTOs.User.UserResponse;

import java.util.List;

public interface AddressService {
    UserResponse add(Long userId, AddressRequest addressRequest);

    List<AddressResponse> getAll(Long userId);

    AddressResponse update(Long userId, UpdateAddressRequest addressRequest);

    void delete(Long userId, Long addressId);


}
