package com.mr_n.userservice.dto;

import com.mr_n.userservice.model.Role;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserRequest {
    private String username;
    private String password;
    private String email;
    private String fullName;
    private Set<Role> roles;
}
