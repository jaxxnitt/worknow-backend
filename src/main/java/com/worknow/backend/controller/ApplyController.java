package com.worknow.backend.controller;

import com.worknow.backend.model.Application;
import com.worknow.backend.model.Gig;
import com.worknow.backend.repository.ApplicationRepository;
import com.worknow.backend.repository.GigRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
public class ApplyController {

    private static final int MAX_APPLICATIONS = 10;

    private final ApplicationRepository appRepo;
    private final GigRepository gigRepo;

    public ApplyController(ApplicationRepository appRepo, GigRepository gigRepo) {
        this.appRepo = appRepo;
        this.gigRepo = gigRepo;
    }

    // ================= APPLY =================
    @PostMapping("/apply/{gigId}")
    public String apply(
            @PathVariable Long gigId,
            @RequestParam String applicantName,
            @RequestParam(required = false) String note
    ) {
        Gig gig = gigRepo.findById(gigId).orElseThrow();

        if (!gig.isActive()) {
            return "Job closed";
        }

        Application app = new Application();
        app.setGigId(gigId);
        app.setApplicantName(applicantName);
        app.setNote(note);
        app.setProcessed(false);

        appRepo.save(app);

        gig.setApplicationCount(gig.getApplicationCount() + 1);

        if (gig.getApplicationCount() >= MAX_APPLICATIONS) {
            gig.setActive(false);
        }

        gigRepo.save(gig);

        return "Applied successfully";
    }

    // ============ CURRENT APPLICANT ============
    @GetMapping("/gigs/{gigId}/current-applicant")
    public Application currentApplicant(
            @PathVariable Long gigId,
            @RequestParam String posterName
    ) {
        Gig gig = gigRepo.findById(gigId).orElseThrow();

        if (!gig.getPosterName().equalsIgnoreCase(posterName)) {
            throw new RuntimeException("Unauthorized");
        }

        List<Application> apps =
                appRepo.findByGigIdAndProcessedFalseOrderByIdAsc(gigId);

        return apps.isEmpty() ? null : apps.get(0);
    }

    // ================= REJECT =================
    @PostMapping("/applications/{id}/reject")
    public void reject(@PathVariable Long id) {
        Application app = appRepo.findById(id).orElseThrow();
        app.setProcessed(true);
        appRepo.save(app);

        Gig gig = gigRepo.findById(app.getGigId()).orElseThrow();

        gig.setApplicationCount(
                Math.max(0, gig.getApplicationCount() - 1)
        );

        if (gig.getApplicationCount() < MAX_APPLICATIONS) {
            gig.setActive(true);
        }

        gigRepo.save(gig);
    }

    // ================= HIRE =================
    @PostMapping("/applications/{id}/hire")
    public void hire(@PathVariable Long id) {
        Application app = appRepo.findById(id).orElseThrow();
        app.setProcessed(true);
        appRepo.save(app);

        Gig gig = gigRepo.findById(app.getGigId()).orElseThrow();
        gig.setActive(false);
        gigRepo.save(gig);
    }
}
