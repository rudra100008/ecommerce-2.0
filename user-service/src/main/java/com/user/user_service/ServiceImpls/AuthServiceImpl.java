package com.user.user_service.ServiceImpls;

import com.shared_library.Exceptions.BusinessInvalidException;
import com.shared_library.Exceptions.ResourceNotFoundException;
import com.shared_library.Utils.JwtUtils;
import com.user.user_service.DTOs.AuthDTO.AuthResponse;
import com.user.user_service.DTOs.AuthDTO.RegisterRequest;
import com.user.user_service.DTOs.Media.MediaUploadResponse;
import com.user.user_service.Entities.User;
import com.user.user_service.Enums.AuthProvider;
import com.user.user_service.Enums.RoleStatus;
import com.user.user_service.Repository.UserRepository;
import com.user.user_service.Services.AuthService;
import com.user.user_service.client.MediaClient;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final MediaClient mediaClient;


    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request, MultipartFile imageFile) {
        if(userRepository.existsByEmail(request.email())){
            throw new BusinessInvalidException("Email already registered.");
        }
        if(userRepository.existsByUsername(request.username())){
            throw  new BusinessInvalidException("Username already taken.");
        }
        MediaUploadResponse uploadResponse = upload(imageFile);

        User user = User.builder()
                .fullName(request.fullName())
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(RoleStatus.ROLE_CUSTOMER)
                .active(true)
                .provider(AuthProvider.LOCAL)
                .imageCustomized(false)
                .publicId(uploadResponse.publicId())
                .imageUrl(uploadResponse.imageUrl())
                .build();
        User saved = this.userRepository.save(user);

        String accessToken = jwtUtils.generateToken(
                saved.getId(),
                saved.getEmail(),
                saved.getRole().name(),
                saved.getProvider().name()
        );
        String refreshToken = jwtUtils.generateRefreshToken(saved.getEmail());


        return new AuthResponse(
                accessToken,
                refreshToken,
                saved.getRole().name(),
                saved.getId()
        );
    }

    @Override
    public AuthResponse login(User user) {
        if(user.getProvider() == AuthProvider.GOOGLE){
            throw new BusinessInvalidException(
                    "This account uses Google login. Please sign in with google"
            );
        }

        if(Boolean.FALSE.equals(user.getActive())){
            throw new BusinessInvalidException("Account is deactivated.");
        }
        String accessToken = jwtUtils.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole().name(),
                user.getProvider().name()
        );
        String refreshToken = jwtUtils.generateRefreshToken(user.getEmail());

        return new AuthResponse(
                accessToken,
                refreshToken,
                user.getRole().name(),
                user.getId()
        );
    }

    @Override
    public AuthResponse refresh(String refreshToken) {
        Claims claims = jwtUtils.validateToken(refreshToken);
        String email = claims.getSubject();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (Boolean.FALSE.equals(user.getActive())) {
            throw new BusinessInvalidException("Account is deactivated");
        }

        String newToken = jwtUtils.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole().name(),
                user.getProvider().name()
        );
        String newRefreshToken = jwtUtils.generateRefreshToken(user.getEmail());


        return new AuthResponse(newToken, newRefreshToken, user.getRole().name(), user.getId());
    }


    private MediaUploadResponse upload(MultipartFile file){
            return  this.mediaClient.uploadImage(file,"user");
    }
}
