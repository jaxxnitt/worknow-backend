package com.worknow.backend.controller;

import com.worknow.backend.dto.ProfileStatsResponse;
import com.worknow.backend.model.ApplicationStatus;
import com.worknow.backend.model.RatingType;
import com.worknow.backend.model.User;
import com.worknow.backend.repository.ApplicationRepository;
import com.worknow.backend.repository.GigRepository;
import com.worknow.backend.repository.RatingRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/me")
@CrossOrigin
public class MeController {

    private final RatingRepository ratingRepo;
    private final ApplicationRepository appRepo;
    private final GigRepository gigRepo;

    public MeController(
            RatingRepository ratingRepo,
            ApplicationRepository appRepo,
            GigRepository gigRepo
    ) {
        this.ratingRepo = ratingRepo;
        this.appRepo = appRepo;
        this.gigRepo = gigRepo;
    }

    @GetMapping
    public ProfileStatsResponse me(
            @AuthenticationPrincipal User user
    ) {
        if (user == null) {
            throw new RuntimeException("Unauthorized");
        }

        ProfileStatsResponse response = new ProfileStatsResponse();

        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setMode(user.getMode().name());
        response.setAvatar(user.getAvatarUrl());
        response.setPortfolioVideoUrl(user.getPortfolioVideoUrl());
        response.setHasPortfolioVideo(user.getPortfolioVideoUrl() != null && !user.getPortfolioVideoUrl().isEmpty());

        Double workerRating = ratingRepo.findAverageRatingByUserIdAndType(user.getId(), RatingType.WORKER_RATING);
        Long workerRatingCount = ratingRepo.countByRatedUserIdAndType(user.getId(), RatingType.WORKER_RATING);
        response.setWorkerRating(workerRating != null ? Math.round(workerRating * 10.0) / 10.0 : null);
        response.setWorkerRatingCount(workerRatingCount != null ? workerRatingCount : 0L);

        Double employerRating = ratingRepo.findAverageRatingByUserIdAndType(user.getId(), RatingType.EMPLOYER_RATING);
        Long employerRatingCount = ratingRepo.countByRatedUserIdAndType(user.getId(), RatingType.EMPLOYER_RATING);
        response.setEmployerRating(employerRating != null ? Math.round(employerRating * 10.0) / 10.0 : null);
        response.setEmployerRatingCount(employerRatingCount != null ? employerRatingCount : 0L);

        Long jobsApplied = appRepo.countByApplicantId(user.getId());
        Long jobsHired = appRepo.countByApplicantIdAndStatus(user.getId(), ApplicationStatus.HIRED);
        Long jobsCompleted = appRepo.countByApplicantIdAndStatus(user.getId(), ApplicationStatus.COMPLETED);
        response.setJobsApplied(jobsApplied != null ? jobsApplied : 0L);
        response.setJobsHired(jobsHired != null ? jobsHired : 0L);
        response.setJobsCompleted(jobsCompleted != null ? jobsCompleted : 0L);

        Long jobsPosted = gigRepo.countByPosterId(user.getId());
        Long completedGigs = gigRepo.countCompletedByPosterId(user.getId());
        Long moneySpent = gigRepo.sumPaymentByPosterIdAndActiveFalse(user.getId());
        response.setJobsPosted(jobsPosted != null ? jobsPosted : 0L);
        response.setPeopleHired(completedGigs != null ? completedGigs : 0L);
        response.setMoneySpent(moneySpent != null ? moneySpent : 0L);

        response.setMoneyEarned(calculateMoneyEarned(user.getId()));

        return response;
    }

    private Long calculateMoneyEarned(Long userId) {
        var completedApps = appRepo.findByApplicantIdAndStatus(userId, ApplicationStatus.COMPLETED);
        long total = 0L;
        for (var app : completedApps) {
            var gig = gigRepo.findById(app.getGigId());
            if (gig.isPresent() && gig.get().getPayment() != null) {
                total += gig.get().getPayment();
            }
        }
        return total;
    }
}
