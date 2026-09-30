package com.exam.dto;

import java.util.List;

public class StudentQuestionResponseDto {

    private Long id;
    private String content;
    private Integer marks;
    private Long examId;
    private List<StudentOptionResponseDto> options;

    public StudentQuestionResponseDto() {
    }

    public StudentQuestionResponseDto(Long id, String content, Integer marks, Long examId, List<StudentOptionResponseDto> options) {
        this.id = id;
        this.content = content;
        this.marks = marks;
        this.examId = examId;
        this.options = options;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getExamId() {
        return examId;
    }

    public void setExamId(Long examId) {
        this.examId = examId;
    }

    public List<StudentOptionResponseDto> getOptions() {
        return options;
    }

    public void setOptions(List<StudentOptionResponseDto> options) {
        this.options = options;
    }
}