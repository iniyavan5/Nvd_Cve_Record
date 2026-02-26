package com.nvd_cve.nvd_cve;

import com.nvd_cve.nvd_cve.repository.CveRepository;
import com.nvd_cve.nvd_cve.service.NvdApiService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NvdCveApplication {
    public static void main(String[] args) {
        SpringApplication.run(NvdCveApplication.class, args);
    }
    @Bean
    CommandLineRunner seedCvesIfEmpty(
            CveRepository repository,
            NvdApiService apiService,
            @Value("${app.sync.seed-on-startup:true}") boolean seedOnStartup
    ) {
        return args -> {
            if (seedOnStartup && repository.count() == 0) {
                apiService.syncFromStartPages(1);
            }

        };
    }
}