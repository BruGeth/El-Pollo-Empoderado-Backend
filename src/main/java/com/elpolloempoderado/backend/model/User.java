package com.elpolloempoderado.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String firstName;
    
    @Column(nullable = false, length = 100)
    private String lastName;
    
    @Column(nullable = false, unique = true, length = 150)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @Column(length = 8)
    private String dni;
    
    private LocalDate birthDate;
    
    /**
     * Relación bidireccional con Address
     * Un usuario puede tener múltiples direcciones de envío
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Address> addresses = new ArrayList<>();
    
    /**
     * Relación bidireccional con Order
     * Un usuario puede tener múltiples pedidos
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Order> orders = new ArrayList<>();
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // ==========================================
    // HELPER METHODS
    // ==========================================
    
    /**
     * Agrega una dirección al usuario
     */
    public void addAddress(Address address) {
        addresses.add(address);
        address.setUser(this);
    }
    
    /**
     * Remueve una dirección del usuario
     */
    public void removeAddress(Address address) {
        addresses.remove(address);
        address.setUser(null);
    }
    
    /**
     * Obtiene la dirección por defecto del usuario
     */
    public Address getDefaultAddress() {
        return addresses.stream()
                .filter(Address::getIsDefault)
                .findFirst()
                .orElse(null);
    }
}