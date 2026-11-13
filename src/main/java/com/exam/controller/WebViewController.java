package com.exam.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class WebViewController {

    @GetMapping({"/", "/login"})
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @GetMapping("/dashboard")
    public String dashboardPage() {
        return "dashboard";
    }
    
    @GetMapping("/teacher-dashboard")
    public String teacherDashboardPage() {
        return "teacher-dashboard";
    }

    @GetMapping("/exam/{id}")
    public String takeExamPage(@PathVariable Long id, Model model) {
        model.addAttribute("examId", id);
        return "take-exam";
    }

    @GetMapping("/result/{resultId}")
    public String resultPage(@PathVariable Long resultId, Model model) {
        model.addAttribute("resultId", resultId);
        return "result";
    }

    @GetMapping("/leaderboard/{examId}")
    public String leaderboardPage(@PathVariable Long examId, Model model) {
        model.addAttribute("examId", examId);
        return "leaderboard";
    }
}