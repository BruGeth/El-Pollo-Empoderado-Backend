package com.elpolloempoderado.backend.config;

import com.elpolloempoderado.backend.model.Role;
import com.elpolloempoderado.backend.model.User;
import com.elpolloempoderado.backend.repository.RoleRepository;
import com.elpolloempoderado.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        initializeRoles();
        initializeAdminUser();
    }
    
    private void initializeRoles() {
        if (roleRepository.findByName("ROLE_USER").isEmpty()) {
            roleRepository.save(new Role(null, "ROLE_USER"));
        }
        
        if (roleRepository.findByName("ROLE_ADMIN").isEmpty()) {
            roleRepository.save(new Role(null, "ROLE_ADMIN"));
        }
    }
    
    private void initializeAdminUser() {
        if (userRepository.findByEmail("admin@empoderado.com").isEmpty()) {
            Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElseThrow();
            
            User admin = new User();
            admin.setFirstName("Admin");
            admin.setLastName("Empoderado");
            admin.setEmail("admin@empoderado.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setDni("12345678");
            admin.setBirthDate(LocalDate.of(1990, 1, 1));
            admin.setAddress("Lima, Perú");
            admin.setRoles(Set.of(adminRole));
            
            userRepository.save(admin);
        }
    }
}