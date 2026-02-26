package com.nvd_cve.nvd_cve.controller;

import com.nvd_cve.nvd_cve.model.CveRecord;
import com.nvd_cve.nvd_cve.repository.CveRepository;
import com.nvd_cve.nvd_cve.service.NvdApiService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CveApiController {
    private final CveRepository repository;
    private final NvdApiService service;
    public CveApiController(CveRepository repository, NvdApiService service) {
        this.repository = repository;
        this.service = service;
    }
    @GetMapping("/")
    public String listCves(Model model, @RequestParam(defaultValue = "0") int page)
    {
        Page<CveRecord> cvePage = repository.findAll(PageRequest.of(page, 20));
        model.addAttribute("cvePage", cvePage);
        return "list";
    }
    @GetMapping("/sync")
    @ResponseBody
    public String sync() {
        service.syncFromStartPages(1);
        return "Sync Completed";
    }
    @GetMapping("/cve/{id}")
    public String getDetails(@PathVariable String id, Model model) {
        CveRecord cve = repository.findByCveId(id).orElse(null);
        model.addAttribute("cve", cve);
        return "detail";
    }
}