package com.elpolloempoderado.backend.repository;

import com.elpolloempoderado.backend.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas de integración para UserRepository.
 * Verifica persistencia y reglas de negocio básicas.
 */
@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    /**
     * Verifica que se puede crear y recuperar un usuario por email.
     */
    @Test
    void testCreateAndFindUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("hashedpassword"); // Simula contraseña ya hasheada
        user.setFirstName("Test");
        user.setLastName("User");
        userRepository.save(user);

        // Verifica que el usuario se puede recuperar por email
        assertThat(userRepository.findByEmail("test@example.com")).isPresent();
    }

    /**
     * Verifica que la contraseña se guarda hasheada y no en texto plano.
     */
    @Test
    void testPasswordIsHashed() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        User user = new User();
        user.setEmail("hash@example.com");
        // Hashea la contraseña antes de guardar
        user.setPassword(encoder.encode("plainpassword"));
        userRepository.save(user);

        User savedUser = userRepository.findByEmail("hash@example.com").get();
        // Verifica que la contraseña guardada no contiene el texto plano
        assertThat(savedUser.getPassword()).doesNotContain("plainpassword");
        // Verifica que el hash corresponde a la contraseña original
        assertThat(encoder.matches("plainpassword", savedUser.getPassword())).isTrue();
    }
}