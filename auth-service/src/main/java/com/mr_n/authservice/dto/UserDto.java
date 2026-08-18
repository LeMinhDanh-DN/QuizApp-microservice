package com.mr_n.authservice.dto;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String fullName;
    private Set<String> roles;
}
