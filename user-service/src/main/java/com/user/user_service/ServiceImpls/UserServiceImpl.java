package com.user.user_service.ServiceImpls;

import com.shared_library.Exceptions.BusinessInvalidException;
import com.shared_library.Exceptions.ImageInvalidException;
import com.shared_library.Exceptions.ResourceNotFoundException;
import com.user.user_service.Constants.PageConstant;
import com.user.user_service.DTOs.Address.AddressResponse;
import com.user.user_service.DTOs.Media.MediaDeleteRequest;
import com.user.user_service.DTOs.Media.MediaUploadResponse;
import com.user.user_service.DTOs.PageInfo;
import com.user.user_service.DTOs.User.ChangePasswordRequest;
import com.user.user_service.DTOs.User.UpdateUserRequest;
import com.user.user_service.DTOs.User.UserResponse;
import com.user.user_service.Entities.CustomUserPrincipal;
import com.user.user_service.Entities.User;
import com.user.user_service.Enums.AuthProvider;
import com.user.user_service.Mapper.AddressMapper;
import com.user.user_service.Mapper.UserMapper;
import com.user.user_service.Repository.AddressRepository;
import com.user.user_service.Repository.UserRepository;
import com.user.user_service.Services.UserService;
import com.user.user_service.Utils.AuthUtil;
import com.user.user_service.client.MediaClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;
    private final MediaClient mediaClient;
    private final PasswordEncoder passwordEncoder;


    private static final List<String> ALLOWED_SORT_FIELDS = List.of(
            "createdAt", "updatedAt","email"
    );


    @Override
    @Transactional(readOnly = true)
    public UserResponse fetchCurrentUser() {
        CustomUserPrincipal principal = AuthUtil.getCustomUserPrincipal();
        Long userId = principal.getId();

        User user = this.userRepository.findByIdWithAddresses(userId)
                .orElseThrow(()-> new ResourceNotFoundException("User not found."));
        
        List<AddressResponse> addressResponseList = this.addressMapper.toAddressResponseList(user.getAddresses());
        
        return this.userMapper.toResponse(user,addressResponseList);
    }



    @Override
    @Transactional
    public UserResponse updateProfile(Long userId, UpdateUserRequest updateRequest) {
        Long loggedInUserId = AuthUtil.getCustomUserPrincipal().getId();
        validateUser(loggedInUserId,userId,null);
        User user = this.userRepository.findById(loggedInUserId)
                .orElseThrow(()-> new ResourceNotFoundException("User not found."));
        if (!Objects.equals(user.getUsername(), updateRequest.username())) {
            user.setUsername(updateRequest.username());
        }
        if (!Objects.equals(user.getPhoneNumber(), updateRequest.phoneNumber())) {
            user.setPhoneNumber(updateRequest.phoneNumber());
        }
        if (!Objects.equals(user.getFullName(), updateRequest.fullName())) {
            user.setFullName(updateRequest.fullName());
        }

        User saved = this.userRepository.save(user);

        List<AddressResponse> addressResponseList = this.addressMapper.toAddressResponseList(saved.getAddresses());
        return this.userMapper.toResponse(saved,addressResponseList);
    }

    @Override
    @Transactional
    public UserResponse updateProfilePic(Long userId, MultipartFile imageFile) {
        Long authUserId = AuthUtil.getCustomUserPrincipal().getId();
        validateUser(authUserId,userId,"User is not allowed to update profile pic.");
        User  user = this.userRepository.findById(authUserId)
                .orElseThrow(()-> new ResourceNotFoundException("User not found"));
        try{
            if(user.getPublicId() != null){
                this.mediaClient.deleteImage(new MediaDeleteRequest(user.getPublicId()));
            }
            MediaUploadResponse uploadResponse = upload(imageFile);
            user.setImageUrl(uploadResponse.imageUrl());
            user.setPublicId(uploadResponse.publicId());
        }catch (Exception e){
            log.error("Failed to update profile pic of user:{}.",user.getEmail());
            throw new ImageInvalidException("Failed to update profile pic");
        }

        User saved = this.userRepository.save(user);
        List<AddressResponse> addressResponseList = this.addressMapper.toAddressResponseList(saved.getAddresses());
        return this.userMapper.toResponse(saved,addressResponseList);
    }

    @Override
    @Transactional
    public void removeProfilePic(Long userId) {
        Long authUserId = AuthUtil.getCustomUserPrincipal().getId();
        validateUser(authUserId, userId, null);

        User user = this.userRepository.findById(authUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Only delete from Cloudinary if it's a custom uploaded image
        if (user.getPublicId() != null) {
            this.mediaClient.deleteImage(new MediaDeleteRequest(user.getPublicId()));
            user.setPublicId(null);
            user.setImageUrl(null);
            user.setImageCustomized(false);
            this.userRepository.save(user);
        }
    }

    @Override
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        Long authUserId = AuthUtil.getCustomUserPrincipal().getId();
        validateUser(authUserId, userId, null);

        User user = this.userRepository.findById(authUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Google users can't change password — they have no local password
        if (user.getProvider() == AuthProvider.GOOGLE) {
            throw new BusinessInvalidException(
                    "This account uses Google login. Password change is not available."
            );
        }

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new BusinessInvalidException("Current password is incorrect.");
        }

        if (request.currentPassword().equals(request.newPassword())) {
            throw new BusinessInvalidException("New password must be different from current password.");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        this.userRepository.save(user);
    }
    @Override
    @Transactional(readOnly = true)
    public PageInfo<UserResponse> getAll(Integer pageNumber, Integer pageSize, String sortBy, String sortDir) {
        String validateSortBy = ALLOWED_SORT_FIELDS.contains(sortBy) ? sortBy : PageConstant.SORT_BY;
        Sort sort = sortDir.equalsIgnoreCase(PageConstant.SORT_DIR)
                ? Sort.by(validateSortBy).descending()
                : Sort.by(validateSortBy).ascending();

        Pageable pageable = PageRequest.of(pageNumber,pageSize,sort);
        Page<User> userPage = this.userRepository.findAllWithAddresses(pageable);

        List<UserResponse> user = userPage.getContent()
                .stream()
                .map(u -> this.userMapper.toResponse(
                        u,
                        this.addressMapper.toAddressResponseList(u.getAddresses())
                ))
                .toList();



        return new PageInfo<>(
                user,
                userPage.getNumber(),
                userPage.getSize(),
                userPage.getTotalElements(),
                userPage.getTotalPages(),
                userPage.isLast()
        );
    }

    @Override
    @Transactional
    public void deactivateUser(Long userId) {
        User  user = this.userRepository.findById(userId)
                .orElseThrow(()-> new ResourceNotFoundException("User not found"));

        if(Boolean.TRUE.equals(user.getActive())){
            user.setActive(false);
            this.userRepository.save(user);
        }

    }

    @Override
    @Transactional
    public void activateUser(Long userId) {
        User  user = this.userRepository.findById(userId)
                .orElseThrow(()-> new ResourceNotFoundException("User not found"));

        if(Boolean.FALSE.equals(user.getActive())){
            user.setActive(true);
            this.userRepository.save(user);
        }

    }

    @Override
    public UserResponse changeRole(Long userId) {
        // for later
        throw new UnsupportedOperationException("This service is not supported for now.");
    }


    // ========== HELPER METHOD
    private void validateUser(Long authUserId,Long userId,String message){
        if(!authUserId.equals(userId)){
            throw new AccessDeniedException(message != null ? message : "User is not allowed to access this service.");
        }
    }

    private MediaUploadResponse upload(MultipartFile imageFile){
        return this.mediaClient.uploadImage(imageFile,"user");
    }
}
