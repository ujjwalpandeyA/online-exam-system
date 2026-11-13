package com.exam.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AiQuestionGenerateRequestDto {

    @NotBlank(message = "Topic cannot be blank")
    private String topic;

    @NotBlank(message = "Difficulty must be specified (e.g., Easy, Medium, Hard)")
    private String difficulty;

    @NotNull(message = "Number of questions is required")
    @Min(value = 1, message = "Must generate at least 1 question")
    @Max(value = 50, message = "Cannot generate more than 50 questions in a single request")
    private Integer numberOfQuestions;

    @NotNull(message = "Default marks per question is required")
    @Min(value = 1, message = "Marks per question must be at least 1")
    private Integer defaultMarks;

    public AiQuestionGenerateRequestDto() {
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public Integer getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public void setNumberOfQuestions(Integer numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
    }

    public Integer getDefaultMarks() {
        return defaultMarks;
    }

    public void setDefaultMarks(Integer defaultMarks) {
        this.defaultMarks = defaultMarks;
    }
}