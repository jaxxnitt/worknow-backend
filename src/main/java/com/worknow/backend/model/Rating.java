package com.worknow.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ratings")
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long raterId;

    private Long ratedUserId;

    private Long gigId;

    @Column(nullable = false)
    private Integer score;

    @Column(length = 500)
    private String comment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RatingType type;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    // ---------- getters & setters ----------

    public Long getId() {
        return id;
    }

    public Long getRaterId() {
        return raterId;
    }

    public void setRaterId(Long raterId) {
        this.raterId = raterId;
    }

    public Long getRatedUserId() {
        return ratedUserId;
    }

    public void setRatedUserId(Long ratedUserId) {
        this.ratedUserId = ratedUserId;
    }

    public Long getGigId() {
        return gigId;
    }

    public void setGigId(Long gigId) {
        this.gigId = gigId;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public RatingType getType() {
        return type;
    }

    public void setType(RatingType type) {
        this.type = type;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
