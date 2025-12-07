package com.example.librarymanagement.controllers;

import com.example.librarymanagement.dtos.models.DashboardModel;
import com.example.librarymanagement.services.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/summary")
    public ResponseEntity<DashboardModel> getSummary() {
        return ResponseEntity.ok(dashboardService.getSummary());
    }
}
