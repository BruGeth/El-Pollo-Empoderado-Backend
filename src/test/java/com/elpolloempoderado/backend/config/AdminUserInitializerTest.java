package com.elpolloempoderado.backend.config;

import com.elpolloempoderado.backend.model.User;
import com.elpolloempoderado.backend.repository.UserRepository;
import com.elpolloempoderado.backend.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AdminUserInitializerTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void adminUserIsCreatedWithAdminRole() throws Exception {
        // Ejecuta manualmente el seeder y el initializer
        DataSeeder seeder = new DataSeeder(roleRepository);
        seeder.run();

        AdminUserInitializer initializer = new AdminUserInitializer();
        initializer.createAdminUser(userRepository, roleRepository).run();

        var adminOpt = userRepository.findByEmail("admin@empoderado.com");
        assertThat(adminOpt).isPresent();

        User admin = adminOpt.get();
        assertThat(admin.getRoles()).anyMatch(role -> "ROLE_ADMIN".equals(role.getName()));
        assertThat(admin.getPassword()).doesNotContain("ContraseñaSegura123!");
    }
}