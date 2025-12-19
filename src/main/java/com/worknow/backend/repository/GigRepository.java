package com.worknow.backend.repository;

import com.worknow.backend.model.Gig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GigRepository extends JpaRepository<Gig, Long> {

    // ✅ Homepage: show only ACTIVE jobs
    List<Gig> findTop50ByActiveTrueOrderByCreatedAtDesc();

    // ✅ City search (case-insensitive, worldwide)
    List<Gig> findTop50ByActiveTrueAndCityContainingIgnoreCaseOrderByCreatedAtDesc(String city);



    List<Gig> findByPosterNameIgnoreCaseOrderByCreatedAtDesc(String posterName);

}
