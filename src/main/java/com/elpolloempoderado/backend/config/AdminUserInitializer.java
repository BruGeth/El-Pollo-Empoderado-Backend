package com.elpolloempoderado.backend.config;

import com.elpolloempoderado.backend.model.User;
import com.elpolloempoderado.backend.model.Role;
import com.elpolloempoderado.backend.repository.UserRepository;
import com.elpolloempoderado.backend.repository.RoleRepository;
import com.elpolloempoderado.backend.util.PasswordUtil;

import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@Profile({ "local" }) // Solo en entornos locales
@DependsOn("dataSeeder") // Asegura que DataSeeder se ejecute primero
public class AdminUserInitializer {

    @Bean
    @Transactional
    public CommandLineRunner createAdminUser(UserRepository userRepository, RoleRepository roleRepository) {
        return args -> {
            String adminEmail = "admin@empoderado.com";
            if (userRepository.findByEmail(adminEmail).isEmpty()) {
                Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                        .orElseThrow(() -> new IllegalStateException("ROLE_ADMIN not found. Run DataSeeder first."));
                User admin = new User();
                admin.setEmail(adminEmail);
                admin.setPassword(PasswordUtil.hash("ContraseñaSegura123!"));
                admin.setFirstName("Admin");
                admin.setLastName("Empoderado");
                admin.setRoles(Set.of(adminRole)); // Asigna el rol existente

                userRepository.save(admin);
            }
        };
    }
}