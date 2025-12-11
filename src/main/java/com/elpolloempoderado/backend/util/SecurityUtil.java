package com.elpolloempoderado.backend.util;

import com.elpolloempoderado.backend.model.User;
import com.elpolloempoderado.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtil {
    
    private static UserRepository userRepository;
    
    // Constructor injection para UserRepository
    public SecurityUtil(UserRepository userRepository) {
        SecurityUtil.userRepository = userRepository;
    }

    public static String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            return ((UserDetails) authentication.getPrincipal()).getUsername();
        }
        return null;
    }
    
    public static Long getCurrentUserId() {
        String email = getCurrentUserEmail();
        if (email != null && userRepository != null) {
            return userRepository.findByEmail(email)
                    .map(User::getId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        }
        throw new RuntimeException("Usuario no autenticado");
    }

    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() 
               && !(authentication.getPrincipal() instanceof String);
    }
}