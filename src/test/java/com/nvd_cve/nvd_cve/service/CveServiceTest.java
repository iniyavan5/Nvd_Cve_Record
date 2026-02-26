package com.nvd_cve.nvd_cve.service;

import com.nvd_cve.nvd_cve.model.CveRecord;
import com.nvd_cve.nvd_cve.repository.CveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(CveService.class)
class CveServiceTest {

    @Autowired
    private CveRepository repository;

    @Autowired
    private CveService cveService;

    @BeforeEach
    void setUp() {
        repository.deleteAll();

        CveRecord a = new CveRecord();
        a.setCveId("CVE-2023-1001");
        a.setCvssScore(9.1);
        a.setPublishedDate(LocalDateTime.now().minusDays(100));
        a.setLastModifiedDate(LocalDateTime.now().minusDays(2));
        a.setSourceIdentifier("cve@mitre.org");
        a.setVulnStatus("Analyzed");

        CveRecord b = new CveRecord();
        b.setCveId("CVE-2024-2222");
        b.setCvssScore(5.0);
        b.setPublishedDate(LocalDateTime.now().minusDays(40));
        b.setLastModifiedDate(LocalDateTime.now().minusDays(20));
        b.setSourceIdentifier("nvd@nist.gov");
        b.setVulnStatus("Modified");

        repository.save(a);
        repository.save(b);
    }

    @Test
    void shouldFilterByYear() {
        Page<CveRecord> result = cveService.search(0, 10, "publishedDate", "desc", null, 2024, null, null);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getCveId()).isEqualTo("CVE-2024-2222");
    }

    @Test
    void shouldFilterByMinimumScore() {
        Page<CveRecord> result = cveService.search(0, 10, "publishedDate", "desc", null, null, 7.0, null);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getCveId()).isEqualTo("CVE-2023-1001");
    }

    @Test
    void shouldFilterByLastModifiedInDays() {
        Page<CveRecord> result = cveService.search(0, 10, "publishedDate", "desc", null, null, null, 7);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getCveId()).isEqualTo("CVE-2023-1001");
    }
}
