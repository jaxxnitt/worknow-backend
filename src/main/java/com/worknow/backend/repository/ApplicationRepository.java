package com.worknow.backend.repository;

import com.worknow.backend.model.Application;
import com.worknow.backend.model.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findByGigIdAndProcessedFalseOrderByIdAsc(Long gigId);

    List<Application> findByApplicantIdOrderByIdDesc(Long applicantId);

    List<Application> findByApplicantIdAndStatus(Long applicantId, ApplicationStatus status);

    @Query("SELECT COUNT(a) FROM Application a WHERE a.applicantId = :applicantId AND a.status = :status")
    Long countByApplicantIdAndStatus(@Param("applicantId") Long applicantId, @Param("status") ApplicationStatus status);

    @Query("SELECT COUNT(a) FROM Application a WHERE a.applicantId = :applicantId")
    Long countByApplicantId(@Param("applicantId") Long applicantId);

    Optional<Application> findByApplicantIdAndGigId(Long applicantId, Long gigId);
}
