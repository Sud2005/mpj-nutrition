package com.nutrition.dss.model;

import jakarta.persistence.*;

/**
 * ============================================================
 *  PERSON 1 - MODEL LAYER
 *  This file defines the User entity (a table in the database).
 *  Each field = one column in the database.
 *
 *  HOW TO TINKER:
 *  - Add a new field like "phoneNumber" → it adds a column
 *  - Change validation logic in isValidPassword()
 *  - Add a new role like "NUTRITIONIST"
 * ============================================================
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ----- TINKER ZONE: Add/rename fields below -----

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;  // stored as plain text for demo; hash in production

    // Role is either "USER" or "ADMIN"
    @Column(nullable = false)
    private String role = "USER";

    // ----- END TINKER ZONE -----

    // One user has one health profile
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private HealthProfile healthProfile;

    // ---- Constructors ----
    public User() {}

    public User(String fullName, String email, String password, String role) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // ---- Simple helper ----
    public boolean isAdmin() {
        return "ADMIN".equals(this.role);
    }

    // ---- Getters & Setters ----
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public HealthProfile getHealthProfile() { return healthProfile; }
    public void setHealthProfile(HealthProfile healthProfile) { this.healthProfile = healthProfile; }
}
