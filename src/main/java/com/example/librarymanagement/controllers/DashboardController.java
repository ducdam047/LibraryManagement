package com.example.librarymanagement.controllers;

import com.example.librarymanagement.dtos.models.*;
import com.example.librarymanagement.dtos.responses.chart.CategoryBorrowStat;
import com.example.librarymanagement.dtos.responses.chart.WeeklyStat;
import com.example.librarymanagement.dtos.responses.dashboard.DashboardCardResponse;
import com.example.librarymanagement.services.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/summary")
    public ResponseEntity<DashboardCardResponse> getSummary() {
        return ResponseEntity.ok(dashboardService.getSummary());
    }

    @GetMapping("/books")
    public ResponseEntity<List<BookModel>> getDashboardBooks(@RequestParam(required = false) String status) {
        List<BookModel> books = dashboardService.getDashboardBooks(status);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserModel>> getDashboardUsers(@RequestParam(required = false) String status) {
        List<UserModel> users = dashboardService.getDashboardUsers(status);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/column-chart")
    public ResponseEntity<List<WeeklyStat>> getColumnChart() {
        List<WeeklyStat> stats = dashboardService.getColumnChart();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/pie-chart")
    public ResponseEntity<List<CategoryBorrowStat>> getPieChart() {
        List<CategoryBorrowStat> stats = dashboardService.getPieChart();
        return ResponseEntity.ok(stats);
    }
}
