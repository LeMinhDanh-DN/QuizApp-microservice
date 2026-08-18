package com.mr_n.authservice.feign;

import com.mr_n.authservice.dto.CreateUserRequest;
import com.mr_n.authservice.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "user-service")
public interface UserClient {

    @PostMapping("/users/internal/create")
    ResponseEntity<UserDto> createUserInternal(@RequestBody CreateUserRequest request);

    @GetMapping("/users/internal/by-username/{username}")
    ResponseEntity<UserDto> getUserByUsernameInternal(@PathVariable("username") String username);
}
