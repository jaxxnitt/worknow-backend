package com.worknow.backend.controller;

import com.worknow.backend.model.*;
import com.worknow.backend.repository.ApplicationRepository;
import com.worknow.backend.repository.GigRepository;
import com.worknow.backend.repository.RatingRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/ratings")
public class RatingController {

    private final RatingRepository ratingRepo;
    private final GigRepository gigRepo;
    private final ApplicationRepository appRepo;

    public RatingController(
            RatingRepository ratingRepo,
            GigRepository gigRepo,
            ApplicationRepository appRepo
    ) {
        this.ratingRepo = ratingRepo;
        this.gigRepo = gigRepo;
        this.appRepo = appRepo;
    }

    @PostMapping("/worker/{gigId}")
    public ResponseEntity<?> rateWorker(
            @PathVariable Long gigId,
            @RequestBody Map<String, Object> body,
            @AuthenticationPrincipal User user
    ) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        Gig gig = gigRepo.findById(gigId).orElse(null);
        if (gig == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Gig not found");
        }

        if (!gig.getPosterId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only the employer can rate the worker");
        }

        if (ratingRepo.existsByRaterIdAndGigId(user.getId(), gigId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Already rated");
        }

        var apps = appRepo.findByGigIdAndProcessedFalseOrderByIdAsc(gigId);
        var hiredApp = appRepo.findAll().stream()
                .filter(a -> a.getGigId().equals(gigId) && a.getStatus() == ApplicationStatus.HIRED)
                .findFirst()
                .orElse(null);

        if (hiredApp == null || hiredApp.getApplicantId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No hired worker found for this gig");
        }

        Integer score = (Integer) body.get("score");
        String comment = (String) body.get("comment");

        if (score == null || score < 1 || score > 5) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Score must be between 1 and 5");
        }

        Rating rating = new Rating();
        rating.setRaterId(user.getId());
        rating.setRatedUserId(hiredApp.getApplicantId());
        rating.setGigId(gigId);
        rating.setScore(score);
        rating.setComment(comment);
        rating.setType(RatingType.WORKER_RATING);
        rating.setCreatedAt(LocalDateTime.now());

        ratingRepo.save(rating);

        return ResponseEntity.ok("Worker rated successfully");
    }

    @PostMapping("/employer/{gigId}")
    public ResponseEntity<?> rateEmployer(
            @PathVariable Long gigId,
            @RequestBody Map<String, Object> body,
            @AuthenticationPrincipal User user
    ) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        Gig gig = gigRepo.findById(gigId).orElse(null);
        if (gig == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Gig not found");
        }

        var myApp = appRepo.findByApplicantIdAndGigId(user.getId(), gigId).orElse(null);
        if (myApp == null || myApp.getStatus() != ApplicationStatus.HIRED && myApp.getStatus() != ApplicationStatus.COMPLETED) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only hired workers can rate the employer");
        }

        if (ratingRepo.existsByRaterIdAndGigId(user.getId(), gigId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Already rated");
        }

        Integer score = (Integer) body.get("score");
        String comment = (String) body.get("comment");

        if (score == null || score < 1 || score > 5) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Score must be between 1 and 5");
        }

        Rating rating = new Rating();
        rating.setRaterId(user.getId());
        rating.setRatedUserId(gig.getPosterId());
        rating.setGigId(gigId);
        rating.setScore(score);
        rating.setComment(comment);
        rating.setType(RatingType.EMPLOYER_RATING);
        rating.setCreatedAt(LocalDateTime.now());

        ratingRepo.save(rating);

        return ResponseEntity.ok("Employer rated successfully");
    }
}
