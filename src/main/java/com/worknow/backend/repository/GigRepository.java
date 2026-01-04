package com.worknow.backend.repository;

import com.worknow.backend.model.Gig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    List<Gig> findByPosterIdOrderByCreatedAtDesc(Long posterId);

    @Query("SELECT COUNT(g) FROM Gig g WHERE g.posterId = :posterId")
    Long countByPosterId(@Param("posterId") Long posterId);

    @Query("SELECT COUNT(g) FROM Gig g WHERE g.posterId = :posterId AND g.active = false")
    Long countCompletedByPosterId(@Param("posterId") Long posterId);

    @Query("SELECT COALESCE(SUM(g.payment), 0) FROM Gig g WHERE g.posterId = :posterId AND g.active = false")
    Long sumPaymentByPosterIdAndActiveFalse(@Param("posterId") Long posterId);
}
