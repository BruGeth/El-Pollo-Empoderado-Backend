package com.elpolloempoderado.backend.config;

import com.elpolloempoderado.backend.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class DataSeederTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void createsDefaultRoles() {
        DataSeeder seeder = new DataSeeder(roleRepository);
        seeder.run();

        assertThat(roleRepository.findByName("ROLE_USER")).isPresent();
        assertThat(roleRepository.findByName("ROLE_ADMIN")).isPresent();
    }
}