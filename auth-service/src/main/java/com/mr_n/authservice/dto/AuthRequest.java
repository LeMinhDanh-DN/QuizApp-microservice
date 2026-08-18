package com.mr_n.authservice.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthRequest {
    private String username;
    private String password;
}
