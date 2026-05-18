package com.user.user_service.Services;


import com.user.user_service.DTOs.PageInfo;
import com.user.user_service.DTOs.User.ChangePasswordRequest;
import com.user.user_service.DTOs.User.UpdateUserRequest;
import com.user.user_service.DTOs.User.UserResponse;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    // fetch current login user
    UserResponse fetchCurrentUser();

    // update profile of user
    UserResponse updateProfile(Long userId, UpdateUserRequest updateRequest);

    // update profile pic of a user
    UserResponse updateProfilePic(Long userId,MultipartFile imageFile);

    // remove profile pic of a user
    void removeProfilePic(Long userId);

    // change password
    void changePassword(Long userId, ChangePasswordRequest request);


    PageInfo<UserResponse> getAll(Integer pageNumber,Integer pageSize,String sortBy,String sortDir);

    void deactivateUser(Long userId);

    void activateUser(Long userId);

    UserResponse changeRole(Long userId);



}
