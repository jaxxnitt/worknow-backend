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

        // 🔐 derive expiry from deadline
        if ("Today".equalsIgnoreCase(gig.getDeadline())) {
            gig.setExpiresAt(
                    LocalDate.now().atTime(23, 59, 59)
            );
        } else if ("Tomorrow".equalsIgnoreCase(gig.getDeadline())) {
            gig.setExpiresAt(
                    LocalDate.now().plusDays(1).atTime(23, 59, 59)
            );
        } else {
            throw new RuntimeException("Invalid deadline value");
        }

        return repo.save(gig);
    }

    // ===============================
    // LIST GIGS (HOMEPAGE + SEARCH)
    // ===============================
    @GetMapping
    public List<Gig> list(@RequestParam(required = false) String city) {

        LocalDateTime now = LocalDateTime.now();

        // 🔹 city search
        if (city != null && !city.trim().isEmpty()) {
            return repo
                    .findTop50ByActiveTrueAndExpiresAtAfterAndCityContainingIgnoreCaseOrderByCreatedAtDesc(
                            now,
                            city.trim()
                    );
        }

        // 🔹 homepage
        return repo
                .findTop50ByActiveTrueAndExpiresAtAfterOrderByCreatedAtDesc(
                        now
                );
    }
}
