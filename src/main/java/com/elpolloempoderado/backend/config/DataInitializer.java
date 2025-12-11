package com.elpolloempoderado.backend.config;

import com.elpolloempoderado.backend.model.Address;
import com.elpolloempoderado.backend.model.City;
import com.elpolloempoderado.backend.model.District;
import com.elpolloempoderado.backend.model.Role;
import com.elpolloempoderado.backend.model.User;
import com.elpolloempoderado.backend.repository.AddressRepository;
import com.elpolloempoderado.backend.repository.CityRepository;
import com.elpolloempoderado.backend.repository.DistrictRepository;
import com.elpolloempoderado.backend.repository.RoleRepository;
import com.elpolloempoderado.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CityRepository cityRepository;
    private final DistrictRepository districtRepository;
    private final AddressRepository addressRepository;
    
    @Override
    public void run(String... args) throws Exception {
        initializeCitiesAndDistricts();
        initializeRoles();
        initializeAdminUser();
    }
    
    /**
     * Inicializa las ciudades y distritos en la base de datos
     */
    private void initializeCitiesAndDistricts() {
        // Solo inicializar si no existen ciudades
        if (cityRepository.count() == 0) {
            // Crear ciudades
            City lima = cityRepository.save(new City(null, "Lima"));
            City callao = cityRepository.save(new City(null, "Callao"));
            City trujillo = cityRepository.save(new City(null, "Trujillo"));
            City chiclayo = cityRepository.save(new City(null, "Chiclayo"));
            City piura = cityRepository.save(new City(null, "Piura"));
            City ica = cityRepository.save(new City(null, "Ica"));
            City huacho = cityRepository.save(new City(null, "Huacho"));
            
            // Crear distritos de Lima
            districtRepository.save(new District(null, "Comas", lima));
            districtRepository.save(new District(null, "Ate", lima));
            districtRepository.save(new District(null, "San Martín de Porres", lima));
            districtRepository.save(new District(null, "Miraflores", lima));
            districtRepository.save(new District(null, "Surco", lima));
            districtRepository.save(new District(null, "Chorrillos", lima));
            districtRepository.save(new District(null, "La Molina", lima));
            districtRepository.save(new District(null, "Santa Anita", lima));
            districtRepository.save(new District(null, "Independencia", lima));
            districtRepository.save(new District(null, "San Miguel", lima));
            districtRepository.save(new District(null, "Lurín", lima));
            districtRepository.save(new District(null, "Cercado de Lima", lima));
            
            // Crear distritos de Callao
            districtRepository.save(new District(null, "Callao (Saenz Peña)", callao));
            districtRepository.save(new District(null, "Callao (Elmer Faucett)", callao));
            
            // Crear distritos de otras ciudades
            districtRepository.save(new District(null, "Trujillo", trujillo));
            districtRepository.save(new District(null, "Chiclayo", chiclayo));
            districtRepository.save(new District(null, "Piura", piura));
            districtRepository.save(new District(null, "Ica", ica));
            districtRepository.save(new District(null, "Huacho", huacho));
        }
    }
    
    private void initializeRoles() {
        if (roleRepository.findByName("ROLE_USER").isEmpty()) {
            roleRepository.save(new Role(null, "ROLE_USER"));
        }
        
        if (roleRepository.findByName("ROLE_ADMIN").isEmpty()) {
            roleRepository.save(new Role(null, "ROLE_ADMIN"));
        }
    }
    
    private void initializeAdminUser() {
        if (userRepository.findByEmail("admin@empoderado.com").isEmpty()) {
            Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElseThrow();
            
            User admin = new User();
            admin.setFirstName("Admin");
            admin.setLastName("Empoderado");
            admin.setEmail("admin@empoderado.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setDni("12345678");
            admin.setBirthDate(LocalDate.of(1990, 1, 1));
            admin.setRoles(Set.of(adminRole));
            
            User savedAdmin = userRepository.save(admin);
            
            // Crear dirección para el admin
            // Buscar Lima y Cercado de Lima (deben existir de initializeCitiesAndDistricts)
            City lima = cityRepository.findAll().stream()
                    .filter(c -> c.getNombre().equals("Lima"))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Ciudad Lima no encontrada"));
            
            District cercadoLima = districtRepository.findAll().stream()
                    .filter(d -> d.getNombre().equals("Cercado de Lima") && d.getCity().getId().equals(lima.getId()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Distrito Cercado de Lima no encontrado"));
            
            Address adminAddress = new Address();
            adminAddress.setUser(savedAdmin);
            adminAddress.setStreet("Av. Javier Prado");
            adminAddress.setNumber("123");
            adminAddress.setPhone("987654321");
            adminAddress.setReference("Edificio principal, frente al parque");
            adminAddress.setCity(lima);
            adminAddress.setDistrict(cercadoLima);
            adminAddress.setLabel("Oficina");
            adminAddress.setIsDefault(true);
            
            addressRepository.save(adminAddress);
        }
    }
}