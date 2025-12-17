package com.worknow.backend.controller;

import com.worknow.backend.model.Application;
import com.worknow.backend.model.Gig;
import com.worknow.backend.repository.ApplicationRepository;
import com.worknow.backend.repository.GigRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@CrossOrigin
@RequestMapping("/apply")
public class ApplyController {

    private final ApplicationRepository appRepo;
    private final GigRepository gigRepo;

    public ApplyController(ApplicationRepository appRepo, GigRepository gigRepo) {
        this.appRepo = appRepo;
        this.gigRepo = gigRepo;
    }

    @PostMapping("/{gigId}")
    public String apply(
            @PathVariable Long gigId,
            @RequestParam String applicantName,
            @RequestParam(required = false) String note
    ) {
        Gig gig = gigRepo.findById(gigId)
                .orElseThrow(() -> new RuntimeException("Gig not found"));

        if (!gig.isActive()) {
            return "Job is closed";
        }

        // ✅ CREATE APPLICATION
        Application app = new Application();
        app.setGigId(gigId);
        app.setApplicantName(applicantName);
        app.setNote(note);
        app.setProcessed(false);

        appRepo.save(app);

        // ✅ INCREMENT APPLICATION COUNT
        gig.setApplicationCount(gig.getApplicationCount() + 1);

        // ✅ CLOSE JOB AT 10
        if (gig.getApplicationCount() >= 10) {
            gig.setActive(false);
        }

        gigRepo.save(gig);

        return "Applied successfully";
    }
}
