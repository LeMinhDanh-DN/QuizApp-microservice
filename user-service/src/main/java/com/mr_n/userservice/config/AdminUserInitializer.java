package com.mr_n.userservice.config;

import com.mr_n.userservice.model.Role;
import com.mr_n.userservice.model.User;
import com.mr_n.userservice.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Set;

@Configuration
public class AdminUserInitializer {

    @Bean
    public CommandLineRunner initAdminUser(UserRepository userRepository) {
        return args -> {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

            // Create admin user if not existing
            if (!userRepository.existsByUsername("admin")) {
                User admin = User.builder()
                        .username("admin")
                        .password(encoder.encode("admin123"))
                        .email("admin@quizapp.com")
                        .fullName("System Admin")
                        .roles(Set.of(Role.ROLE_ADMIN, Role.ROLE_USER))
                        .build();
                userRepository.save(admin);
                System.out.println(">>> Default ADMIN account created: username='admin', password='admin123'");
            } else {
                // Ensure existing admin user has ROLE_ADMIN
                userRepository.findByUsername("admin").ifPresent(user -> {
                    if (!user.getRoles().contains(Role.ROLE_ADMIN)) {
                        user.getRoles().add(Role.ROLE_ADMIN);
                        userRepository.save(user);
                        System.out.println(">>> Updated 'admin' account with ROLE_ADMIN");
                    }
                });
            }
        };
    }
}
