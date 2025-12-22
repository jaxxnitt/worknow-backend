package com.worknow.backend.controller;

import com.worknow.backend.model.Gig;
import com.worknow.backend.repository.GigRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/gigs")
@CrossOrigin
public class GigController {

    private final GigRepository repo;

    public GigController(GigRepository repo) {
        this.repo = repo;
    }

    // ===============================
    // CREATE GIG
    // ===============================
    @PostMapping
    public Gig create(@RequestBody Gig gig) {

        LocalDateTime now = LocalDateTime.now();
        gig.setCreatedAt(now);
        gig.setActive(true);

        // Convert "Today / Tomorrow" into real expiry
        if ("Tomorrow".equalsIgnoreCase(gig.getDeadline())) {
            gig.setExpiresAt(now.plusHours(48));
        } else {
            // Default = Today
            gig.setExpiresAt(now.plusHours(24));
        }

        return repo.save(gig);
    }


    // ===============================
    // LIST GIGS (HOMEPAGE + SEARCH)
    // ===============================
    @GetMapping
    public List<Gig> list(@RequestParam(required = false) String city) {

        LocalDateTime now = LocalDateTime.now();

        if (city != null && !city.trim().isEmpty()) {
            return repo
                    .findTop50ByActiveTrueAndExpiresAtAfterAndCityContainingIgnoreCaseOrderByCreatedAtDesc(
                            now,
                            city.trim()
                    );
        }

        return repo.findTop50ByActiveTrueAndExpiresAtAfterOrderByCreatedAtDesc(now);
    }

}
