package com.example.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "USERS")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String username;

    private String password;

    private String email;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    // New fields
    private String fullName;
    
    @Enumerated(EnumType.STRING)
    private Gender gender;
    
    private Integer birthYear;
    
    private String phoneNumber;
    
    private boolean emailVerified;
    
    private String emailVerificationToken;
    
    private LocalDateTime emailVerificationTokenExpiry;
}
