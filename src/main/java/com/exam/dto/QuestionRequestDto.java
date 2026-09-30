package com.exam.dto;

import java.util.List;

public class QuestionRequestDto {

    private String content;
    private Integer marks;
    private List<OptionRequestDto> options;

    public QuestionRequestDto() {
    }

    public QuestionRequestDto(String content, Integer marks, List<OptionRequestDto> options) {
        this.content = content;
        this.marks = marks;
        this.options = options;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getMarks() {
        return marks;
    }

    public void setMarks(Integer marks) {
        this.marks = marks;
    }

    public List<OptionRequestDto> getOptions() {
        return options;
    }

    public void setOptions(List<OptionRequestDto> options) {
        this.options = options;
    }
}