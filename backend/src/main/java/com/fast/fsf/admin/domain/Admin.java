package com.fast.fsf.admin.domain;

import jakarta.persistence.*;

/**
 * Entity representing an administrator in the system.
 */
@Entity
@Table(name = "admins")
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column
    private String name;

    public Admin() {}

    public Admin(String email, String name) {
        this.email = email.toLowerCase();
        this.name  = name;
    }

    public Long   getId()    { return id; }
    public String getEmail() { return email; }
    public void   setEmail(String e) { this.email = e.toLowerCase(); }
    public String getName()  { return name; }
    public void   setName(String n)  { this.name = n; }
}
