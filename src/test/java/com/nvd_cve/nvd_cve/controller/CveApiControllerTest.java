package com.nvd_cve.nvd_cve.controller;

import com.nvd_cve.nvd_cve.model.CveRecord;
import com.nvd_cve.nvd_cve.service.NvdApiService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CveApiController.class)
@AutoConfigureMockMvc(addFilters = false)
class CveApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CveService cveService;

    @MockBean
    private NvdApiService nvdApiService;

    @Test
    void shouldReturnPaginatedCveList() throws Exception {
        CveRecord record = new CveRecord();
        record.setCveId("CVE-2024-0001");
        record.setSourceIdentifier("cve@mitre.org");
        record.setVulnStatus("Analyzed");
        record.setCvssScore(8.8);
        record.setPublishedDate(LocalDateTime.now().minusDays(5));

        Page<CveRecord> page = new PageImpl<>(List.of(record), PageRequest.of(0, 10), 1);
        Mockito.when(cveService.search(0, 10, "publishedDate", "desc", null, null, null, null))
                .thenReturn(page);

        mockMvc.perform(get("/api/cves").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].cveId").value("CVE-2024-0001"));
    }

    @Test
    void shouldReturnSingleCveById() throws Exception {
        CveRecord record = new CveRecord();
        record.setCveId("CVE-2024-0099");
        record.setDescription("Test description");
        record.setRawCveJson("{\"metrics\":{\"cvssMetricV31\":[{\"cvssData\":{\"version\":\"3.1\",\"baseScore\":7.5,\"vectorString\":\"AV:N/AC:L\"},\"baseSeverity\":\"HIGH\"}]}}");

        Mockito.when(cveService.findByCveId("CVE-2024-0099")).thenReturn(Optional.of(record));

        mockMvc.perform(get("/api/cves/CVE-2024-0099"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cveId").value("CVE-2024-0099"));
    }

    @Test
    void shouldReturnDetailedCvePayload() throws Exception {
        CveRecord record = new CveRecord();
        record.setCveId("CVE-2024-0002");
        record.setDescription("Detailed record");
        record.setRawCveJson("{\"metrics\":{\"cvssMetricV31\":[{\"cvssData\":{\"version\":\"3.1\",\"baseScore\":9.1,\"vectorString\":\"AV:N/AC:L\"},\"baseSeverity\":\"CRITICAL\"}]},\"configurations\":[]}");
        Mockito.when(cveService.findByCveId("CVE-2024-0002")).thenReturn(Optional.of(record));

        mockMvc.perform(get("/api/cves/CVE-2024-0002/detail"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cveId").value("CVE-2024-0002"))
                .andExpect(jsonPath("$.cvss.baseScore").value(9.1));
    }

    @Test
    void shouldTriggerIncrementalSync() throws Exception {
        Mockito.when(nvdApiService.syncModifiedSinceLastKnown()).thenReturn(50L);
        Mockito.when(cveService.count()).thenReturn(120L);

        mockMvc.perform(post("/api/cves/sync/incremental"))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.processed").value(50))
                .andExpect(jsonPath("$.totalRecords").value(120));
    }
}
