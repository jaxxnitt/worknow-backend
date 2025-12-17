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

    // =====================================================
    // 🆕 STEP 1: GET ALL JOBS BY POSTER NAME
    // =====================================================
    @GetMapping("/manage/jobs")
    public List<Gig> getJobsByPoster(@RequestParam String posterName) {
        return gigRepo.findByPosterNameIgnoreCaseOrderByCreatedAtDesc(
                posterName.trim()
        );
    }

    // =====================================================
    // 🆕 STEP 2: GET CURRENT APPLICANT (NO POSTER NAME NEEDED)
    // =====================================================
    @GetMapping("/manage/gigs/{gigId}/current-applicant")
    public Application currentApplicant(@PathVariable Long gigId) {

        List<Application> pending =
                appRepo.findByGigIdAndProcessedFalseOrderByIdAsc(gigId);

        return pending.isEmpty() ? null : pending.get(0);
    }

    // =====================================================
    // REJECT (UNCHANGED LOGIC)
    // =====================================================
    @PostMapping("/applications/{id}/reject")
    public void reject(@PathVariable Long id) {

        Application current = appRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));

        current.setProcessed(true);
        appRepo.save(current);

        Gig gig = gigRepo.findById(current.getGigId()).orElseThrow();
        gig.setApplicationCount(
                Math.max(0, gig.getApplicationCount() - 1)
        );

        if (gig.getApplicationCount() < 10) {
            gig.setActive(true);
        }

        gigRepo.save(gig);
    }

    // =====================================================
    // HIRE (UNCHANGED LOGIC)
    // =====================================================
    @PostMapping("/applications/{id}/hire")
    public void hire(@PathVariable Long id) {

        Application hired = appRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));

        hired.setProcessed(true);
        appRepo.save(hired);

        Gig gig = gigRepo.findById(hired.getGigId()).orElseThrow();
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

