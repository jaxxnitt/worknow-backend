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

    @PostMapping
    public Gig create(@RequestBody Gig gig) {
        gig.setCreatedAt(LocalDateTime.now());
        return repo.save(gig);
    }

    @GetMapping
    public List<Gig> list() {
        return repo.findTop50ByOrderByCreatedAtDesc();
    }
}
