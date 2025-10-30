package com.elpolloempoderado.backend.repository;

import com.elpolloempoderado.backend.model.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void canCreateAndFindRole() {
        Role role = new Role("ROLE_TEST");
        roleRepository.save(role);

        var found = roleRepository.findByName("ROLE_TEST");
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("ROLE_TEST");
    }
}