package com.exam.controller;

import com.exam.dto.AiQuestionGenerateRequestDto;
import com.exam.dto.QuestionRequestDto;
import com.exam.dto.QuestionResponseDto;
import com.exam.dto.StudentQuestionResponseDto;
import com.exam.service.AiQuestionService;
import com.exam.service.QuestionService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/questions")
public class QuestionController {

    private final QuestionService questionService;
    private final AiQuestionService aiQuestionService;

    public QuestionController(QuestionService questionService, AiQuestionService aiQuestionService) {
        this.questionService = questionService;
        this.aiQuestionService = aiQuestionService;
    }

    // Add a manual question to an exam (Only TEACHER)
    // POST http://localhost:8081/api/v1/questions/exam/{examId}
    @PostMapping("/exam/{examId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<QuestionResponseDto> addQuestion(
            @PathVariable Long examId,
            @RequestBody QuestionRequestDto requestDto) {
        QuestionResponseDto createdQuestion = questionService.addQuestionToExam(examId, requestDto);
        return new ResponseEntity<>(createdQuestion, HttpStatus.CREATED);
    }
    @GetMapping("/exam/{examId}/student")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER')")
    public ResponseEntity<List<StudentQuestionResponseDto>> getQuestionsForStudent(@PathVariable Long examId) {
        return ResponseEntity.ok(questionService.getQuestionsForStudent(examId));
    }

    // AI Question Generation (Only TEACHER)
    // POST http://localhost:8081/api/v1/questions/exam/{examId}/ai-generate
    @PostMapping("/exam/{examId}/ai-generate")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<QuestionResponseDto>> generateQuestionsWithAi(
            @PathVariable Long examId,
            @Valid @RequestBody AiQuestionGenerateRequestDto requestDto,
            Principal principal) {
        return new ResponseEntity<>(
                aiQuestionService.generateAndSaveQuestions(examId, requestDto, principal.getName()),
                HttpStatus.CREATED
        );
    }

    // Get all questions belonging to an exam
    // GET http://localhost:8081/api/v1/questions/exam/{examId}
    @GetMapping("/exam/{examId}")
    public ResponseEntity<List<QuestionResponseDto>> getQuestionsByExamId(@PathVariable Long examId) {
        List<QuestionResponseDto> questions = questionService.getQuestionsByExamId(examId);
        return ResponseEntity.ok(questions);
    }

    // Delete a question by ID (Only TEACHER)
    // DELETE http://localhost:8081/api/v1/questions/{questionId}
    @DeleteMapping("/{questionId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<String> deleteQuestion(@PathVariable Long questionId) {
        questionService.deleteQuestion(questionId);
        return ResponseEntity.ok("Question deleted successfully with id: " + questionId);
    }
}