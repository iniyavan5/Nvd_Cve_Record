package com.nvd_cve.nvd_cve.service;

import com.nvd_cve.nvd_cve.model.CveRecord;
import com.nvd_cve.nvd_cve.repository.CveRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
@Service
public class NvdApiService {
    private final CveRepository repository;
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String BASE_URL =
            "https://services.nvd.nist.gov/rest/json/cves/2.0";
    public NvdApiService(CveRepository repository) {
        this.repository = repository;
    }
    public void syncFromStartPages(int pages) {
        int startIndex = 0;
        int resultsPerPage = 100;

        for (int i = 0; i < pages; i++) {
            fetchAndStoreCves(startIndex, resultsPerPage);
            startIndex += resultsPerPage;
        }
    }
    public int fetchAndStoreCves(int startIndex, int resultsPerPage) {

        String url = BASE_URL +
                "?startIndex=" + startIndex +
                "&resultsPerPage=" + resultsPerPage;
        String response = restTemplate.getForObject(url, String.class);
        JSONObject jsonObject = new JSONObject(response);
        JSONArray vulnerabilities = jsonObject.getJSONArray("vulnerabilities");
        for (int i = 0; i < vulnerabilities.length(); i++) {
            JSONObject cveJson = vulnerabilities.getJSONObject(i)
                    .getJSONObject("cve");
            String cveId = cveJson.getString("id");
            if (repository.existsByCveId(cveId)) {
                continue;
            }
            CveRecord cve = new CveRecord();
            cve.setCveId(cveId);
            cve.setIdentifier("cve@mitre.org");
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
            cve.setPublishedDate(LocalDateTime.parse(cveJson.getString("published"), formatter));
            cve.setLastModifiedDate(LocalDateTime.parse(cveJson.getString("lastModified"), formatter));
            JSONArray descArray = cveJson.getJSONArray("descriptions");
            cve.setDescription(descArray.getJSONObject(0).getString("value"));
            Double cvssScore = null;
            if (cveJson.has("metrics")) {
                JSONObject metrics = cveJson.getJSONObject("metrics");
                if (metrics.has("cvssMetricV31")) {
                    JSONArray arr = metrics.getJSONArray("cvssMetricV31");
                    cvssScore = arr.getJSONObject(0)
                            .getJSONObject("cvssData")
                            .getDouble("baseScore");
                }
                else if (metrics.has("cvssMetricV30")) {
                    JSONArray arr = metrics.getJSONArray("cvssMetricV30");
                    cvssScore = arr.getJSONObject(0)
                            .getJSONObject("cvssData")
                            .getDouble("baseScore");
                }
                else if (metrics.has("cvssMetricV2")) {
                    JSONArray arr = metrics.getJSONArray("cvssMetricV2");
                    cvssScore = arr.getJSONObject(0)
                            .getJSONObject("cvssData")
                            .getDouble("baseScore");
                }
            }
            cve.setCvssScore(cvssScore);
            cve.setStatus("Analyzed");
            repository.save(cve);
        }
        return vulnerabilities.length();
    }
}