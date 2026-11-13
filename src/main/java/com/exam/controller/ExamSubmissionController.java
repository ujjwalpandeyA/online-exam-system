package com.exam.controller;

import com.exam.dto.ExamResultResponseDto;
import com.exam.dto.ExamReviewResponseDto;
import com.exam.dto.ExamSubmissionDto;
import com.exam.dto.LeaderboardEntryDto;
import com.exam.service.ExamSubmissionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/submissions")
public class ExamSubmissionController {

    private final ExamSubmissionService submissionService;

    public ExamSubmissionController(ExamSubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    // Submit an exam attempt (STUDENT only)
    @PostMapping("/exam/{examId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ExamResultResponseDto> submitExam(
            @PathVariable Long examId,
            @RequestBody ExamSubmissionDto submissionDto,
            Principal principal) {
        ExamResultResponseDto result = submissionService.submitExam(examId, submissionDto, principal.getName());
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    // Student fetches their own submission history
    @GetMapping("/my-results")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<ExamResultResponseDto>> getMyResults(Principal principal) {
        List<ExamResultResponseDto> results = submissionService.getMyResults(principal.getName());
        return ResponseEntity.ok(results);
    }
    
    @GetMapping("/exam/{examId}/leaderboard")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER')")
    public ResponseEntity<List<LeaderboardEntryDto>> getExamLeaderboard(@PathVariable Long examId) {
        return ResponseEntity.ok(submissionService.getExamLeaderboard(examId));
    }

    // Teacher views all submissions for an exam they created
    @GetMapping("/exam/{examId}/results")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<ExamResultResponseDto>> getExamResultsForTeacher(
            @PathVariable Long examId,
            Principal principal) {
        List<ExamResultResponseDto> results = submissionService.getExamResultsForTeacher(examId, principal.getName());
        return ResponseEntity.ok(results);
    }
    @GetMapping("/{resultId}/review")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER')")
    public ResponseEntity<ExamReviewResponseDto> getExamReview(
            @PathVariable Long resultId,
            Principal principal) {
        return ResponseEntity.ok(submissionService.getExamReview(resultId, principal.getName()));
    }

    // View specific result by ID (STUDENT or TEACHER)
    @GetMapping("/{resultId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER')")
    public ResponseEntity<ExamResultResponseDto> getResultById(@PathVariable Long resultId) {
        ExamResultResponseDto result = submissionService.getResultById(resultId);
        return ResponseEntity.ok(result);
    }
}