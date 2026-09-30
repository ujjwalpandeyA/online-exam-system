package com.exam.controller;
import java.security.Principal;

import com.exam.dto.ExamRequestDto;
import com.exam.dto.ExamResponseDto;
import com.exam.service.ExamService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/exams")
public class ExamController {

    private final ExamService examService;

    public ExamController(ExamService examService) {
        this.examService = examService;
    }

 // Only TEACHER can create an exam
 // POST http://localhost:8081/api/v1/exams
 @PostMapping
 @PreAuthorize("hasRole('TEACHER')")
 public ResponseEntity<ExamResponseDto> createExam(
         @RequestBody ExamRequestDto requestDto,
         Principal principal) {
     ExamResponseDto createdExam = examService.createExam(requestDto, principal.getName());
     return new ResponseEntity<>(createdExam, HttpStatus.CREATED);
 }

    // Any authenticated user can view an exam by ID
    // GET http://localhost:8081/api/v1/exams/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ExamResponseDto> getExamById(@PathVariable Long id) {
        return ResponseEntity.ok(examService.getExamById(id));
    }

    // Any authenticated user can view published exams
    // GET http://localhost:8081/api/v1/exams/published
    @GetMapping("/published")
    public ResponseEntity<List<ExamResponseDto>> getPublishedExams() {
        return ResponseEntity.ok(examService.getAllPublishedExams());
    }

    // View exams created by a teacher
    // GET http://localhost:8081/api/v1/exams/teacher/{teacherId}
    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<ExamResponseDto>> getExamsByTeacher(@PathVariable Long teacherId) {
        return ResponseEntity.ok(examService.getExamsByTeacher(teacherId));
    }

    // Only TEACHER can publish an exam
    // PUT http://localhost:8081/api/v1/exams/{id}/publish
    @PutMapping("/{id}/publish")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ExamResponseDto> publishExam(@PathVariable Long id) {
        return ResponseEntity.ok(examService.publishExam(id));
    }
}