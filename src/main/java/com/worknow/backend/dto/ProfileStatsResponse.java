package com.worknow.backend.dto;

public class ProfileStatsResponse {

    private Long id;
    private String name;
    private String email;
    private String mode;
    private String avatar;

    private Double workerRating;
    private Long workerRatingCount;
    private Double employerRating;
    private Long employerRatingCount;

    private Long moneyEarned;
    private Long moneySpent;

    private Long jobsApplied;
    private Long jobsHired;
    private Long jobsCompleted;

    private Long jobsPosted;
    private Long peopleHired;

    // ---------- getters & setters ----------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Double getWorkerRating() {
        return workerRating;
    }

    public void setWorkerRating(Double workerRating) {
        this.workerRating = workerRating;
    }

    public Long getWorkerRatingCount() {
        return workerRatingCount;
    }

    public void setWorkerRatingCount(Long workerRatingCount) {
        this.workerRatingCount = workerRatingCount;
    }

    public Double getEmployerRating() {
        return employerRating;
    }

    public void setEmployerRating(Double employerRating) {
        this.employerRating = employerRating;
    }

    public Long getEmployerRatingCount() {
        return employerRatingCount;
    }

    public void setEmployerRatingCount(Long employerRatingCount) {
        this.employerRatingCount = employerRatingCount;
    }

    public Long getMoneyEarned() {
        return moneyEarned;
    }

    public void setMoneyEarned(Long moneyEarned) {
        this.moneyEarned = moneyEarned;
    }

    public Long getMoneySpent() {
        return moneySpent;
    }

    public void setMoneySpent(Long moneySpent) {
        this.moneySpent = moneySpent;
    }

    public Long getJobsApplied() {
        return jobsApplied;
    }

    public void setJobsApplied(Long jobsApplied) {
        this.jobsApplied = jobsApplied;
    }

    public Long getJobsHired() {
        return jobsHired;
    }

    public void setJobsHired(Long jobsHired) {
        this.jobsHired = jobsHired;
    }

    public Long getJobsCompleted() {
        return jobsCompleted;
    }

    public void setJobsCompleted(Long jobsCompleted) {
        this.jobsCompleted = jobsCompleted;
    }

    public Long getJobsPosted() {
        return jobsPosted;
    }

    public void setJobsPosted(Long jobsPosted) {
        this.jobsPosted = jobsPosted;
    }

    public Long getPeopleHired() {
        return peopleHired;
    }

    public void setPeopleHired(Long peopleHired) {
        this.peopleHired = peopleHired;
    }
}
