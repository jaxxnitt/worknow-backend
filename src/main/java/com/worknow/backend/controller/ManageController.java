package com.worknow.backend.controller;

import com.worknow.backend.model.Application;
import com.worknow.backend.model.Gig;
import com.worknow.backend.model.User;
import com.worknow.backend.repository.ApplicationRepository;
import com.worknow.backend.repository.GigRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/manage")
public class ManageController {

    private final ApplicationRepository appRepo;
    private final GigRepository gigRepo;

    public ManageController(
            ApplicationRepository appRepo,
            GigRepository gigRepo
    ) {
        this.appRepo = appRepo;
        this.gigRepo = gigRepo;
    }

    @GetMapping("/jobs")
    public List<Gig> getMyJobs(
            @AuthenticationPrincipal User user
    ) {
        if (user == null) {
            throw new RuntimeException("Unauthorized");
        }

        return gigRepo.findByPosterNameIgnoreCaseOrderByCreatedAtDesc(
                user.getName()
        );
    }

    @GetMapping("/gigs/{gigId}/current-applicant")
    public Application currentApplicant(
            @PathVariable Long gigId,
            @AuthenticationPrincipal User user
    ) {
        if (user == null) {
            throw new RuntimeException("Unauthorized");
        }

        Gig gig = gigRepo.findById(gigId)
                .orElseThrow(() -> new RuntimeException("Gig not found"));

        if (!gig.getPosterName().equalsIgnoreCase(user.getName())) {
            throw new RuntimeException("Unauthorized");
        }

        List<Application> pending =
                appRepo.findByGigIdAndProcessedFalseOrderByIdAsc(gigId);

        return pending.isEmpty() ? null : pending.get(0);
    }

    @PostMapping("/applications/{id}/reject")
    public void reject(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        if (user == null) {
            throw new RuntimeException("Unauthorized");
        }

        Application current = appRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));

        Gig gig = gigRepo.findById(current.getGigId())
                .orElseThrow(() -> new RuntimeException("Gig not found"));

        if (!gig.getPosterName().equalsIgnoreCase(user.getName())) {
            throw new RuntimeException("Unauthorized");
        }

        current.setProcessed(true);
        appRepo.save(current);

        gig.setApplicationCount(
                Math.max(0, gig.getApplicationCount() - 1)
        );

        if (gig.getApplicationCount() < 10) {
            gig.setActive(true);
        }

        gigRepo.save(gig);
    }

    @PostMapping("/applications/{id}/hire")
    public void hire(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        if (user == null) {
            throw new RuntimeException("Unauthorized");
        }

        Application hired = appRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));

        Gig gig = gigRepo.findById(hired.getGigId())
                .orElseThrow(() -> new RuntimeException("Gig not found"));

        if (!gig.getPosterName().equalsIgnoreCase(user.getName())) {
            throw new RuntimeException("Unauthorized");
        }

        hired.setProcessed(true);
        appRepo.save(hired);

        gig.setActive(false);
        gigRepo.save(gig);

        List<Application> rest =
                appRepo.findByGigIdAndProcessedFalseOrderByIdAsc(
                        hired.getGigId()
                );

        rest.forEach(a -> a.setProcessed(true));
        appRepo.saveAll(rest);
    }
}
