package com.worknow.backend.repository;

import com.worknow.backend.model.Application;
import com.worknow.backend.model.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    // ===== EXISTING (KEEP) =====




    // (Optional future use — safe to keep)
    List<Application> findByGigIdAndProcessedFalseOrderByIdAsc(Long gigId);
}
