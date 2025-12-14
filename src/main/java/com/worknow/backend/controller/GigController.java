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

    // ✅ CREATE GIG (unchanged)
    @PostMapping
    public Gig create(@RequestBody Gig gig) {
        gig.setCreatedAt(LocalDateTime.now());
        return repo.save(gig);
    }

    // ✅ LIST GIGS (WITH OPTIONAL CITY FILTER)
    @GetMapping
    public List<Gig> list(@RequestParam(required = false) String city) {

        // If city is provided → filter
        if (city != null && !city.trim().isEmpty()) {
            return repo.findTop50ByCityContainingIgnoreCaseOrderByCreatedAtDesc(
                    city.trim()
            );
        }

        // Otherwise → existing behavior
        return repo.findTop50ByOrderByCreatedAtDesc();
    }
}
