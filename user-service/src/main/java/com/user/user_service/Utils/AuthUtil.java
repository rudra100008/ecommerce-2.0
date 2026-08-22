package com.user.user_service.Utils;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.shared_library.Security.AuthenticatedUser;

public class AuthUtil {

    @NotNull
    public static  AuthenticatedUser getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() ||
                auth instanceof AnonymousAuthenticationToken) {
            throw new AccessDeniedException("Not authenticated");
        }
        return (AuthenticatedUser) Objects.requireNonNull(auth.getPrincipal());
    }
}
