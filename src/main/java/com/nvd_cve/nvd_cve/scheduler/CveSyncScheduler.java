package com.nvd_cve.nvd_cve.scheduler;

import com.nvd_cve.nvd_cve.service.NvdApiService;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CveSyncScheduler {
    private final NvdApiService service;
    public CveSyncScheduler(NvdApiService service) {
        this.service = service;
    }
    @Scheduled(cron = "0 0 2 * * ?")
    public void syncCves() {
        service.fetchAndStoreCves(0, 100);
    }
}