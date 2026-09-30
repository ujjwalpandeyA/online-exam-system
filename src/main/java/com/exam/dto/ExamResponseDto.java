package com.exam.dto;

public class ExamResponseDto {

    private Long id;
    private String title;
    private String description;
    private Integer totalMarks;
    private Integer passMarks;
    private Integer durationMinutes;
    private boolean published;
    private String createdByTeacher;

    public ExamResponseDto() {
    }

    public ExamResponseDto(Long id, String title, String description, Integer totalMarks, Integer passMarks, Integer durationMinutes, boolean published, String createdByTeacher) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.totalMarks = totalMarks;
        this.passMarks = passMarks;
        this.durationMinutes = durationMinutes;
        this.published = published;
        this.createdByTeacher = createdByTeacher;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public String getCreatedByTeacher() {
        return createdByTeacher;
    }

    public void setCreatedByTeacher(String createdByTeacher) {
        this.createdByTeacher = createdByTeacher;
    }
}