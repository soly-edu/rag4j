package com.example.rag.controller;

import com.example.rag.service.AdvancedRagService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 接入層：對外暴露 RESTful API
 */
@RestController
public class RagController {

    private final AdvancedRagService advancedRagService;

    public RagController(AdvancedRagService advancedRagService) {
        this.advancedRagService = advancedRagService;
    }

    @GetMapping("/ask")
    public String askQuestion(@RequestParam(defaultValue = "Apple") String query) {
        return advancedRagService.processQuery(query);
    }
}