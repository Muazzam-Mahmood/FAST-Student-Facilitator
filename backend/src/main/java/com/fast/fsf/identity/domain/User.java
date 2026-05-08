package com.fast.fsf.identity.domain;

import jakarta.persistence.*;

/**
 * Entity representing a user in the system.
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.STUDENT;

    @Column(nullable = false)
    private boolean banned = false;

    // --- CONSTRUCTORS ---
    public User() {}

    public User(String name, String email) {
        this.name = name;
        this.email = email;
        this.role = Role.STUDENT;
        this.banned = false;
    }

    public User(String name, String email, Role role) {
        this.name = name;
        this.email = email;
        this.role = role;
        this.banned = false;
    }

    // --- GETTERS AND SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public boolean isBanned() { return banned; }
    public void setBanned(boolean banned) { this.banned = banned; }
}
