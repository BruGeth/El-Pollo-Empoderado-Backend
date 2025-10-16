package com.elpolloempoderado.backend.repository;

import com.elpolloempoderado.backend.model.Role;
import com.elpolloempoderado.backend.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;

    @Test
    void shouldFindUserByEmail() {
        // Given
        Role userRole = roleRepository.save(new Role(null, "ROLE_USER"));
        
        User user = new User();
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail("test@example.com");
        user.setPassword("hashedPassword");
        user.setDni("12345678");
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        user.setAddress("Test Address");
        user.setRoles(Set.of(userRole));
        
        userRepository.save(user);
        
        // When
        var foundUser = userRepository.findByEmail("test@example.com");
        
        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getFirstName()).isEqualTo("Test");
        assertThat(foundUser.get().getRoles()).hasSize(1);
    }

    @Test
    void shouldCheckIfEmailExists() {
        // Given
        Role userRole = roleRepository.save(new Role(null, "ROLE_USER"));
        
        User user = new User();
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail("existing@example.com");
        user.setPassword("hashedPassword");
        user.setRoles(Set.of(userRole));
        
        userRepository.save(user);
        
        // When & Then
        assertThat(userRepository.existsByEmail("existing@example.com")).isTrue();
        assertThat(userRepository.existsByEmail("nonexistent@example.com")).isFalse();
    }
}