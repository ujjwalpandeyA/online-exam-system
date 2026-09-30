package com.exam.service;

import com.exam.dto.AiQuestionGenerateRequestDto;
import com.exam.dto.QuestionResponseDto;

import java.util.List;

public interface AiQuestionService {

    List<QuestionResponseDto> generateAndSaveQuestions(
            Long examId,
            AiQuestionGenerateRequestDto requestDto,
            String teacherUsername
    );
}