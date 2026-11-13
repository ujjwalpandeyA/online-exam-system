package com.exam.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ExamReviewResponseDto(
    Long resultId,
    String examTitle,
    String studentName,
    Integer totalMarks,
    Integer marksObtained,
    boolean passed,
    LocalDateTime submittedAt,
    List<QuestionReviewDto> questionReviews
) {}