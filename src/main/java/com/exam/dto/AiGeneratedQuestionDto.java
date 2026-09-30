package com.exam.dto;

import java.util.List;

public class AiGeneratedQuestionDto {

    private String questionText;
    private List<AiGeneratedOptionDto> options;

    public AiGeneratedQuestionDto() {}

    public AiGeneratedQuestionDto(String questionText, List<AiGeneratedOptionDto> options) {
        this.questionText = questionText;
        this.options = options;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public List<AiGeneratedOptionDto> getOptions() {
        return options;
    }

    public void setOptions(List<AiGeneratedOptionDto> options) {
        this.options = options;
    }
}