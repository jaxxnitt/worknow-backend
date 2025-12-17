package com.worknow.backend.controller;

import com.worknow.backend.model.Application;
import com.worknow.backend.model.Gig;
import com.worknow.backend.repository.ApplicationRepository;
import com.worknow.backend.repository.GigRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
public class ManageController {

    private final ApplicationRepository appRepo;
    private final GigRepository gigRepo;

    public ManageController(ApplicationRepository appRepo, GigRepository gigRepo) {
        this.appRepo = appRepo;
        this.gigRepo = gigRepo;
    }

    // ================= CURRENT APPLICANT =================
    @GetMapping("/gigs/{gigId}/current-applicant")
    public Application currentApplicant(
            @PathVariable Long gigId,
            @RequestParam String posterName
    ) {
        // authorize poster
        gigRepo.findByIdAndPosterName(gigId, posterName)
                .orElseThrow(() -> new RuntimeException("Unauthorized"));

        // first unprocessed applicant
        List<Application> pending =
                appRepo.findByGigIdAndProcessedFalseOrderByIdAsc(gigId);

        return pending.isEmpty() ? null : pending.get(0);
    }

    // ================= REJECT =================
    @PostMapping("/applications/{id}/reject")
    public void reject(@PathVariable Long id) {

        Application current = appRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));

        current.setProcessed(true);
        appRepo.save(current);

        // decrease count & reopen job if needed
        Gig gig = gigRepo.findById(current.getGigId()).orElseThrow();
        gig.setApplicationCount(
                Math.max(0, gig.getApplicationCount() - 1)
        );

        if (gig.getApplicationCount() < 10) {
            gig.setActive(true);
        }

        gigRepo.save(gig);
    }

    // ================= HIRE =================
    @PostMapping("/applications/{id}/hire")
    public void hire(@PathVariable Long id) {

        Application hired = appRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));

        hired.setProcessed(true);
        appRepo.save(hired);

        // permanently close job
        Gig gig = gigRepo.findById(hired.getGigId()).orElseThrow();
        gig.setActive(false);
        gigRepo.save(gig);

        // reject all remaining applicants
        List<Application> rest =
                appRepo.findByGigIdAndProcessedFalseOrderByIdAsc(
                        hired.getGigId()
                );

        rest.forEach(a -> a.setProcessed(true));
        appRepo.saveAll(rest);
    }
}
