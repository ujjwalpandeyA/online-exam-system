package com.exam.dto;

public record QuestionReviewDto(
    Long questionId,
    String questionContent,
    Integer marks,
    Long selectedOptionId,
    String selectedOptionText,
    Long correctOptionId,
    String correctOptionText,
    boolean isCorrect
) {}