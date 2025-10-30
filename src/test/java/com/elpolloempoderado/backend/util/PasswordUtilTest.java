package com.elpolloempoderado.backend.util;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class PasswordUtilTest {

    @Test
    void hashesPasswordCorrectly() {
        String plain = "mySecret123";
        String hashed = PasswordUtil.hash(plain);

        assertThat(hashed).isNotEqualTo(plain);
        assertThat(hashed).startsWith("$2a$"); // BCrypt hash prefix
    }
}