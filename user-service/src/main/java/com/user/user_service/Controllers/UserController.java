package com.user.user_service.Controllers;

import com.user.user_service.DTOs.User.ChangePasswordRequest;
import com.user.user_service.DTOs.User.UpdateUserRequest;
import com.user.user_service.DTOs.User.UserResponse;
import com.user.user_service.Services.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;


    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(){
        UserResponse response = this.userService.fetchCurrentUser();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateUser(
            @Valid @RequestBody UpdateUserRequest request,
            @RequestHeader("X-User-Id") Long userId
    ){
        UserResponse userResponse = this.userService.updateProfile(userId,request);
        return ResponseEntity.status(HttpStatus.OK).body(userResponse);
    }

    @PostMapping(value = "/image",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> updateUserImage(
            @RequestPart("image")MultipartFile imageFile,
            @RequestHeader("X-User-Id") Long userId
    ){
        UserResponse userResponse = this.userService.updateProfilePic(userId,imageFile);
        return ResponseEntity.status(HttpStatus.OK).body(userResponse);
    }

    @DeleteMapping("/image")
    public ResponseEntity<?> deleteImage(
            @RequestHeader("X-User-Id") Long userId
    ){
        this.userService.removeProfilePic(userId);
        return ResponseEntity.noContent().build();
    }


    @PutMapping("/password")
    public ResponseEntity<?> changePassword(
            HttpServletResponse response,
            @Valid @RequestBody ChangePasswordRequest changePasswordRequest,
            @RequestHeader("X-User-Id") Long userId
    ){
        this.userService.changePassword(userId,changePasswordRequest);

        ResponseCookie clearAccess = ResponseCookie.from("accessToken","")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();

        ResponseCookie clearRefresh = ResponseCookie.from("refreshToken","")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE,clearAccess.toString());
        response.addHeader(HttpHeaders.SET_COOKIE,clearRefresh.toString());

        return ResponseEntity.ok(Map.of(
                "message","Password changed successfully. Please login again"
        ));
    }



}
