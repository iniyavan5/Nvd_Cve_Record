package com.nvd_cve.nvd_cve.repository;

import com.nvd_cve.nvd_cve.model.CveRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CveRepository extends JpaRepository<CveRecord, Long> {

    boolean existsByCveId(String cveId);
    Optional<CveRecord> findByCveId(String cveId);
}