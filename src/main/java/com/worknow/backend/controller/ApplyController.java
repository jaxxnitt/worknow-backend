package com.worknow.backend.controller;

import com.worknow.backend.model.Application;
import com.worknow.backend.model.Gig;
import com.worknow.backend.model.User;
import com.worknow.backend.repository.ApplicationRepository;
import com.worknow.backend.repository.GigRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/apply")
public class ApplyController {

    private final ApplicationRepository appRepo;
    private final GigRepository gigRepo;

    public ApplyController(
            ApplicationRepository appRepo,
            GigRepository gigRepo
    ) {
        this.appRepo = appRepo;
        this.gigRepo = gigRepo;
    }

    @PostMapping("/{gigId}")
    public ResponseEntity<?> apply(
            @PathVariable Long gigId,
            @RequestBody(required = false) Map<String, String> body,
            @AuthenticationPrincipal User user
    ) {
        if (user == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication required");
        }

        Gig gig = gigRepo.findById(gigId)
                .orElseThrow(() -> new RuntimeException("Gig not found"));

        if (!gig.isActive()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Job is closed");
        }

        String note = body != null ? body.get("note") : null;

        Application app = new Application();
        app.setGigId(gigId);
        app.setApplicantName(user.getName());
        app.setNote(note);
        app.setProcessed(false);

        appRepo.save(app);

        gig.setApplicationCount(gig.getApplicationCount() + 1);
        if (gig.getApplicationCount() >= 10) {
            gig.setActive(false);
        }

        gigRepo.save(gig);

        return ResponseEntity.ok("Applied successfully");
    }
}
