package com.worknow.backend.repository;

import com.worknow.backend.model.Rating;
import com.worknow.backend.model.RatingType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    List<Rating> findByRatedUserIdAndTypeOrderByCreatedAtDesc(Long ratedUserId, RatingType type);

    @Query("SELECT AVG(r.score) FROM Rating r WHERE r.ratedUserId = :userId AND r.type = :type")
    Double findAverageRatingByUserIdAndType(@Param("userId") Long userId, @Param("type") RatingType type);

    @Query("SELECT COUNT(r) FROM Rating r WHERE r.ratedUserId = :userId AND r.type = :type")
    Long countByRatedUserIdAndType(@Param("userId") Long userId, @Param("type") RatingType type);

    Optional<Rating> findByRaterIdAndGigId(Long raterId, Long gigId);

    boolean existsByRaterIdAndGigId(Long raterId, Long gigId);
}
