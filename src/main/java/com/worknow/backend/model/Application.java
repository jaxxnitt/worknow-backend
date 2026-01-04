package com.worknow.backend.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long gigId;

    private Long applicantId;

    private String applicantName;

    @Column(length = 1000)
    private String note;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ApplicationStatus status = ApplicationStatus.PENDING;

    // ✅ REQUIRED FOR REJECT / HIRE FLOW
    private boolean processed = false;

    // ---------- getters & setters ----------

    public Long getId() {
        return id;
    }

    public Long getGigId() {
        return gigId;
    }

    public void setGigId(Long gigId) {
        this.gigId = gigId;
    }

    public Long getApplicantId() {
        return applicantId;
    }

    public void setApplicantId(Long applicantId) {
        this.applicantId = applicantId;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }
}
