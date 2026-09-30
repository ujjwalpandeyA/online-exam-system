package com.exam.dto;

import java.util.List;

public class ExamSubmissionDto {

    private Long studentId;
    private List<AnswerSubmissionDto> answers;

    public ExamSubmissionDto() {
    }

    public ExamSubmissionDto(Long studentId, List<AnswerSubmissionDto> answers) {
        this.studentId = studentId;
        this.answers = answers;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public List<AnswerSubmissionDto> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnswerSubmissionDto> answers) {
        this.answers = answers;
    }
}