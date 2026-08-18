package com.mr_n.authservice.dto;

import lombok.*;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserRequest {
    private String username;
    private String password;
    private String email;
    private String fullName;
    private Set<String> roles;
}
