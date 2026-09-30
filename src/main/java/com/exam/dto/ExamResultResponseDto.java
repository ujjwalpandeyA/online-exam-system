package com.exam.dto;

import java.time.LocalDateTime;

public class ExamResultResponseDto {

    private Long resultId;
    private String examTitle;
    private String studentName;
    private Integer totalQuestions;
    private Integer correctAnswers;
    private Integer wrongAnswers;
    private Integer totalMarks;
    private Integer marksObtained;
    private Integer passMarks;
    private boolean passed;
    private LocalDateTime submittedAt;

    public ExamResultResponseDto() {
    }

    public ExamResultResponseDto(Long resultId, String examTitle, String studentName, Integer totalQuestions,
                                 Integer correctAnswers, Integer wrongAnswers, Integer totalMarks,
                                 Integer marksObtained, Integer passMarks, boolean passed, LocalDateTime submittedAt) {
        this.resultId = resultId;
        this.examTitle = examTitle;
        this.studentName = studentName;
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
        this.wrongAnswers = wrongAnswers;
        this.totalMarks = totalMarks;
        this.marksObtained = marksObtained;
        this.passMarks = passMarks;
        this.passed = passed;
        this.submittedAt = submittedAt;
    }

    public Long getResultId() {
        return resultId;
    }

    public void setResultId(Long resultId) {
        this.resultId = resultId;
    }

    public String getExamTitle() {
        return examTitle;
    }

    public void setExamTitle(String examTitle) {
        this.examTitle = examTitle;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public Integer getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(Integer totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public Integer getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(Integer correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public Integer getWrongAnswers() {
        return wrongAnswers;
    }

    public void setWrongAnswers(Integer wrongAnswers) {
        this.wrongAnswers = wrongAnswers;
    }

    public Integer getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(Integer totalMarks) {
        this.totalMarks = totalMarks;
    }

    public Integer getMarksObtained() {
        return marksObtained;
    }

    public void setMarksObtained(Integer marksObtained) {
        this.marksObtained = marksObtained;
    }

    public Integer getPassMarks() {
        return passMarks;
    }

    public void setPassMarks(Integer passMarks) {
        this.passMarks = passMarks;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }
}