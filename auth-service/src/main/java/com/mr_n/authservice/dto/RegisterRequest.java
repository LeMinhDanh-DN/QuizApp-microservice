package com.mr_n.authservice.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {
    private String username;
    private String password;
    private String email;
    private String fullName;
}
