package com.user.user_service.SecurityConfig;

import com.user.user_service.Entities.User;
import com.user.user_service.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;


    @NotNull
    @Override
    public User loadUserByUsername(@NotNull String email) throws UsernameNotFoundException {
        return this.userRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException(String.format("Email %s not found.",email)));

    }
}
