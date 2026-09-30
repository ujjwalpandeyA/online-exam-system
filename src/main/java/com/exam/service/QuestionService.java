package com.exam.service;

import com.exam.dto.QuestionRequestDto;
import com.exam.dto.QuestionResponseDto;
import com.exam.dto.StudentQuestionResponseDto;

import java.util.List;

public interface QuestionService {

    QuestionResponseDto addQuestionToExam(Long examId, QuestionRequestDto requestDto);

    List<QuestionResponseDto> getQuestionsByExamId(Long examId);

    List<StudentQuestionResponseDto> getQuestionsForStudent(Long examId);

    void deleteQuestion(Long questionId);
}