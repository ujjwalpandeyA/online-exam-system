package com.exam.service;

import com.exam.dto.ExamRequestDto;
import com.exam.dto.ExamResponseDto;

import java.util.List;

public interface ExamService {

	ExamResponseDto createExam(ExamRequestDto requestDto, String username);

    ExamResponseDto getExamById(Long examId);

    List<ExamResponseDto> getAllPublishedExams();

    List<ExamResponseDto> getExamsByTeacher(Long teacherId);

    ExamResponseDto publishExam(Long examId);
}