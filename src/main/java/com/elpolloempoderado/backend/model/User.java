package com.elpolloempoderado.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    
    @Column(length = 255)
    private String address;
    
    @Column(length = 20)
    private String telefono;
    
    @Column(length = 255)
    private String referenceHome;
    
    // Relación opcional con City (ciudad)
    @ManyToOne
    @JoinColumn(name = "ciudad_id")
    private City city;
    
    // Relación opcional con District (distrito)
    @ManyToOne
    @JoinColumn(name = "distrito_id")
    private District district;
    
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
}