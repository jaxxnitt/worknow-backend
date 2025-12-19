package com.worknow.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Gig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private Integer payment;
    private String city;
    private String posterName;

    // "Today" or "Tomorrow" (UI-only meaning)
    private String deadline;

    // ✅ REAL expiry logic (backend truth)
    private LocalDateTime expiresAt;

    private int applicationCount = 0;

    private boolean active = true;

    private LocalDateTime createdAt;

    // ---------- getters & setters ----------

    public Long getId() { return id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Integer getPayment() { return payment; }
    public void setPayment(Integer payment) { this.payment = payment; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getPosterName() { return posterName; }
    public void setPosterName(String posterName) { this.posterName = posterName; }

    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public int getApplicationCount() { return applicationCount; }
    public void setApplicationCount(int applicationCount) {
        this.applicationCount = applicationCount;
    }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
