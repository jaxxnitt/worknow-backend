package com.worknow.backend.repository;

import com.worknow.backend.model.Gig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface GigRepository extends JpaRepository<Gig, Long> {

    List<Gig> findTop50ByActiveTrueAndExpiresAtAfterOrderByCreatedAtDesc(
            LocalDateTime now
    );

    List<Gig> findTop50ByActiveTrueAndExpiresAtAfterAndCityContainingIgnoreCaseOrderByCreatedAtDesc(
            LocalDateTime now,
            String city
    );

    List<Gig> findByPosterNameIgnoreCaseOrderByCreatedAtDesc(String posterName);
}
