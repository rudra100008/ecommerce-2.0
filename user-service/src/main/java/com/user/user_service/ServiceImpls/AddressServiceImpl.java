package com.user.user_service.ServiceImpls;

import com.shared_library.Exceptions.BusinessInvalidException;
import com.shared_library.Exceptions.ResourceNotFoundException;
import com.user.user_service.DTOs.Address.AddressRequest;
import com.user.user_service.DTOs.Address.AddressResponse;
import com.user.user_service.DTOs.Address.UpdateAddressRequest;
import com.user.user_service.DTOs.User.UserResponse;
import com.user.user_service.Entities.Address;
import com.user.user_service.Entities.User;
import com.user.user_service.Mapper.AddressMapper;
import com.user.user_service.Mapper.UserMapper;
import com.user.user_service.Repository.AddressRepository;
import com.user.user_service.Repository.UserRepository;
import com.user.user_service.Services.AddressService;
import com.user.user_service.Utils.AuthUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressServiceImpl implements AddressService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;
    @Override
    public UserResponse add(Long userId, AddressRequest addressRequest) {
        Long authUserId = AuthUtil.getCustomUserPrincipal().getId();
        validateUser(authUserId,userId,"User is not allowed to add address.");
        User user = this.userRepository.findByIdWithAddresses(authUserId)
                .orElseThrow(()-> new ResourceNotFoundException("User not found."));
        if (user.getAddresses().size() >= 4) {
            throw new BusinessInvalidException("Maximum 4 addresses allowed per user.");
        }
        Address address = Address.builder()
                .district(addressRequest.district())
                .municipality(addressRequest.municipality())
                .province(addressRequest.province())
                .landmark(addressRequest.landmark())
                .wardNumber(addressRequest.wardNumber())
                .build();
        user.addAddress(address);
        User saved = this.userRepository.save(user);
        List<AddressResponse> addressResponseList = this.addressMapper.toAddressResponseList(saved.getAddresses());
        return this.userMapper.toResponse(saved,addressResponseList);
    }

    @Override
    public List<AddressResponse> getAll(Long userId) {
        User user = this.userRepository.findById(userId)
                .orElseThrow(()-> new ResourceNotFoundException("User not found."));
        return this.addressMapper.toAddressResponseList(user.getAddresses());
    }

    @Override
    @Transactional
    public AddressResponse update(Long userId, UpdateAddressRequest addressRequest) {
        Long authUserId = AuthUtil.getCustomUserPrincipal().getId();
        validateUser(authUserId,userId,"User is not allowed to add address.");

        Address existingAddress = this.addressRepository.findById(addressRequest.addressId())
                .orElseThrow(()-> new ResourceNotFoundException("Address not found"));
        if (!Objects.equals(existingAddress.getUser().getId(), authUserId)) {
            throw new AccessDeniedException("Address does not belong to this user.");
        }

        if (!Objects.equals(existingAddress.getDistrict(), addressRequest.district())){
            existingAddress.setDistrict(addressRequest.district());
        }
        if(!Objects.equals(existingAddress.getProvince(),addressRequest.province())){
            existingAddress.setProvince(addressRequest.province());
        }

        if(!Objects.equals(existingAddress.getMunicipality(),addressRequest.municipality())){
            existingAddress.setMunicipality(addressRequest.municipality());
        }

        if(!Objects.equals(existingAddress.getLandmark(),addressRequest.landmark())){
            existingAddress.setLandmark(addressRequest.landmark());
        }

        if(!Objects.equals(existingAddress.getWardNumber(),addressRequest.wardNumber())){
            existingAddress.setWardNumber(addressRequest.wardNumber());
        }

        Address saved = this.addressRepository.save(existingAddress);
        return this.addressMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(Long userId, Long addressId) {
        Long authUserId = AuthUtil.getCustomUserPrincipal().getId();
        validateUser(authUserId, userId, "Not allowed to delete this address.");

        Address address = this.addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found."));

        // Verify address belongs to this user
        if (!address.getUser().getId().equals(authUserId)) {
            throw new AccessDeniedException("Address does not belong to this user.");
        }

        this.addressRepository.delete(address);
    }

    // ========== HELPER METHOD ==========
    private void validateUser(Long authUserId,Long userId,String message){
        if(!authUserId.equals(userId)){
            throw new AccessDeniedException(message != null ? message : "User is not allowed to access this service.");
        }
    }
}
