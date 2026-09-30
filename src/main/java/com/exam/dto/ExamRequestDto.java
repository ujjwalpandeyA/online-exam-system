package com.exam.dto;

public class ExamRequestDto {

    private String title;
    private String description;
    private Integer totalMarks;
    private Integer passMarks;
    private Integer durationMinutes;
    private Long teacherId;

    public ExamRequestDto() {
    }

    public ExamRequestDto(String title, String description, Integer totalMarks, Integer passMarks, Integer durationMinutes, Long teacherId) {
        this.title = title;
        this.description = description;
        this.totalMarks = totalMarks;
        this.passMarks = passMarks;
        this.durationMinutes = durationMinutes;
        this.teacherId = teacherId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(Integer totalMarks) {
        this.totalMarks = totalMarks;
    }

    public Integer getPassMarks() {
        return passMarks;
    }

    public void setPassMarks(Integer passMarks) {
        this.passMarks = passMarks;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }
}