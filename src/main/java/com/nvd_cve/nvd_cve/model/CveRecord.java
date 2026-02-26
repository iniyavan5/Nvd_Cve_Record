package com.nvd_cve.nvd_cve.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@Entity
@Table(name = "cve_records")
public class CveRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String cveId;
    private String identifier;
    @Column(columnDefinition = "TEXT")
    private String description;
    private Double cvssScore;
    private String status;
    private LocalDateTime publishedDate;
    private LocalDateTime lastModifiedDate;

}