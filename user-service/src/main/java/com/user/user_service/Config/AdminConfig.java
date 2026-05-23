package com.user.user_service.Config;

import com.user.user_service.Entities.User;
import com.user.user_service.Enums.AuthProvider;
import com.user.user_service.Enums.RoleStatus;
import com.user.user_service.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminConfig implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(AdminConfig.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        createAdmin();
    }

    private void createAdmin(){
        boolean adminExists = this.userRepository.existsByRole(RoleStatus.ROLE_ADMIN);
        if(!adminExists){
            User admin = User.builder()
                    .username("admin")
                    .fullName("Admin User")
                    .email("admin123@gmail.com")
                    .password(passwordEncoder.encode("admin123456789"))
                    .role(RoleStatus.ROLE_ADMIN)
                    .provider(AuthProvider.LOCAL)
                    .active(true)
                    .imageCustomized(false)
                    .build();

            userRepository.save(admin);
            log.info("Admin created");
        }
    }
}
