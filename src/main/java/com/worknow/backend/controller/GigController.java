package com.worknow.backend.controller;

import com.worknow.backend.model.Gig;
import com.worknow.backend.repository.GigRepository;
import org.springframework.web.bind.annotation.*;

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

    // ✅ CREATE GIG
    @PostMapping
    public Gig create(@RequestBody Gig gig) {

        gig.setCreatedAt(LocalDateTime.now());

        // ensure new job is active
        gig.setActive(true);

        return repo.save(gig);
    }

    // ✅ LIST GIGS (HOMEPAGE + CITY SEARCH)
    @GetMapping
    public List<Gig> list(@RequestParam(required = false) String city) {

        // ✅ City filter (search)
        if (city != null && !city.trim().isEmpty()) {
            return repo
                    .findTop50ByActiveTrueAndCityContainingIgnoreCaseOrderByCreatedAtDesc(
                            city.trim()
                    );
        }

        // ✅ Homepage: show latest ACTIVE jobs
        return repo.findTop50ByActiveTrueOrderByCreatedAtDesc();
    }
}
