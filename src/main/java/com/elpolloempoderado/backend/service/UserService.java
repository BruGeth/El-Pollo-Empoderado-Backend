package com.elpolloempoderado.backend.service;

import com.elpolloempoderado.backend.dto.ChangePasswordRequest;
import com.elpolloempoderado.backend.dto.UpdateUserRequest;
import com.elpolloempoderado.backend.dto.UserDTO;
import com.elpolloempoderado.backend.model.Role;
import com.elpolloempoderado.backend.model.User;
import com.elpolloempoderado.backend.repository.UserRepository;
import com.elpolloempoderado.backend.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Page<UserDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(this::convertToUserDTO);
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return convertToUserDTO(user);
    }

    public UserDTO getCurrentUser() {
        String email = SecurityUtil.getCurrentUserEmail();
        if (email == null) {
            throw new RuntimeException("User not authenticated");
        }
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return convertToUserDTO(user);
    }

    public UserDTO updateCurrentUser(UpdateUserRequest request) {
        String email = SecurityUtil.getCurrentUserEmail();
        if (email == null) {
            throw new RuntimeException("User not authenticated");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Actualizar campos
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getDni() != null) {
            user.setDni(request.getDni());
        }
        if (request.getBirthDate() != null) {
            user.setBirthDate(request.getBirthDate());
        }
        // Nota: Los campos de dirección (address, telefono, reference_home, ciudad, distrito)
        // ahora se manejan a través de la tabla addresses con múltiples direcciones por usuario

        User savedUser = userRepository.save(user);
        return convertToUserDTO(savedUser);
    }

    public void changePassword(ChangePasswordRequest request) {
        String email = SecurityUtil.getCurrentUserEmail();
        if (email == null) {
            throw new RuntimeException("User not authenticated");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verificar contraseña actual
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        // Actualizar contraseña
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    private UserDTO convertToUserDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getDni(),
                user.getBirthDate(),
                null, // address - deprecated
                null, // telefono - deprecated
                null, // referenceHome - deprecated
                null, // cityId - deprecated
                null, // cityName - deprecated
                null, // districtId - deprecated
                null, // districtName - deprecated
                user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet()),
                user.getCreatedAt()
        );
    }
}