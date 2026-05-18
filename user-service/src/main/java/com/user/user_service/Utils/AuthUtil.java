package com.user.user_service.Utils;

import com.shared_library.Exceptions.BusinessInvalidException;
import com.user.user_service.Entities.CustomUserPrincipal;
import com.user.user_service.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthUtil {
    private final UserRepository userRepository;

    @NotNull
    public static CustomUserPrincipal getCustomUserPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken){
            throw new AccessDeniedException("Not authenticated");
        }

        CustomUserPrincipal principal =
                (CustomUserPrincipal) authentication.getPrincipal();
        if (principal == null) {
            throw new BusinessInvalidException("User is not authenticated");
        }
        return principal;
    }
}
