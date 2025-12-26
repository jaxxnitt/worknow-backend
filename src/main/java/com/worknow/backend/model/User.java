package com.worknow.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/*
 * IMPORTANT:
 * Do NOT name the table "user".
 * "user" is a reserved keyword in H2, Postgres, MySQL.
 */
@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "providerUserId")
        }
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * OAuth provider name (google, github, etc.)
     */
    @Column(nullable = false)
    private String provider;

    /*
     * Unique user ID from the OAuth provider.
     * Example: Google "sub" field.
     */
    @Column(nullable = false, unique = true)
    private String providerUserId;

    @Column(nullable = false)
    private String email;

    private String name;

    private String avatarUrl;

    /*
     * Store enum as STRING for portability.
     * Never use native SQL enums.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserMode mode;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    // ---------------- getters & setters ----------------

    public Long getId() {
        return id;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getProviderUserId() {
        return providerUserId;
    }

    public void setProviderUserId(String providerUserId) {
        this.providerUserId = providerUserId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public UserMode getMode() {
        return mode;
    }

    public void setMode(UserMode mode) {
        this.mode = mode;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
