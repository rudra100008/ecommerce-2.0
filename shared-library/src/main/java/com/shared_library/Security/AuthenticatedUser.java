package com.shared_library.Security;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class AuthenticatedUser {
    private String userId;
    private String email;
    private String role;
}
