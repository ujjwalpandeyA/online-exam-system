package com.exam.service;

import com.exam.dto.ExamResultResponseDto;
import com.exam.dto.ExamSubmissionDto;

import java.util.List;

public interface ExamSubmissionService {

    ExamResultResponseDto submitExam(Long examId, ExamSubmissionDto submissionDto, String username);

    ExamResultResponseDto getResultById(Long resultId);

    List<ExamResultResponseDto> getResultsByStudent(Long studentId);

    List<ExamResultResponseDto> getMyResults(String username);

    List<ExamResultResponseDto> getExamResultsForTeacher(Long examId, String teacherUsername);
}