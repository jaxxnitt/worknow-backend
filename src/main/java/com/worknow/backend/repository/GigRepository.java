package com.worknow.backend.repository;

import com.worknow.backend.model.Gig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GigRepository extends JpaRepository<Gig, Long> {
    List<Gig> findTop50ByOrderByCreatedAtDesc();
}

