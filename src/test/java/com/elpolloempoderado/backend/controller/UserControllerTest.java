package com.elpolloempoderado.backend.controller;

import com.elpolloempoderado.backend.dto.ChangePasswordRequest;
import com.elpolloempoderado.backend.dto.UpdateUserRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "admin@empoderado.com", roles = {"ADMIN"})
    void shouldGetAllUsersAsAdmin() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = {"USER"})
    void shouldRejectGetAllUsersAsUser() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@empoderado.com", roles = {"ADMIN"})
    void shouldGetUserByIdAsAdmin() throws Exception {
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("admin@empoderado.com"));
    }

    @Test
    @WithMockUser(username = "admin@empoderado.com", roles = {"ADMIN"})
    void shouldGetCurrentUserProfile() throws Exception {
        mockMvc.perform(get("/api/user/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("admin@empoderado.com"));
    }

    @Test
    @WithMockUser(username = "admin@empoderado.com", roles = {"ADMIN"})
    void shouldUpdateCurrentUserProfile() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest(
                "Updated", "Name", "87654321", 
                LocalDate.of(1985, 5, 15)
        );

        mockMvc.perform(put("/api/user/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Updated"))
                .andExpect(jsonPath("$.lastName").value("Name"));
    }

    @Test
    @WithMockUser(username = "admin@empoderado.com", roles = {"ADMIN"})
    void shouldChangePassword() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest("admin123", "newpassword123");

        mockMvc.perform(put("/api/user/me/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password changed successfully"));
    }

    @Test
    @WithMockUser(username = "admin@empoderado.com", roles = {"ADMIN"})
    void shouldRejectWrongOldPassword() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest("wrongpassword", "newpassword123");

        mockMvc.perform(put("/api/user/me/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Current password is incorrect"));
    }
}