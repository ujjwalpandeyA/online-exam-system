package com.exam.service;

import com.exam.dto.AiGeneratedOptionDto;
import com.exam.dto.AiGeneratedQuestionDto;
import com.exam.dto.AiQuestionGenerateRequestDto;
import com.exam.dto.OptionResponseDto;
import com.exam.dto.QuestionResponseDto;
import com.exam.entity.Exam;
import com.exam.entity.Option;
import com.exam.entity.Question;
import com.exam.exception.BadRequestException;
import com.exam.exception.ResourceNotFoundException;
import com.exam.repository.ExamRepository;
import com.exam.repository.OptionRepository;
import com.exam.repository.QuestionRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AiQuestionServiceImpl implements AiQuestionService {

    private final ChatClient chatClient;
    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final OptionRepository optionRepository;
    private final ObjectMapper objectMapper;

    public AiQuestionServiceImpl(
            ChatClient.Builder chatClientBuilder,
            ExamRepository examRepository,
            QuestionRepository questionRepository,
            OptionRepository optionRepository,
            ObjectMapper objectMapper) {
        this.chatClient = chatClientBuilder.build();
        this.examRepository = examRepository;
        this.questionRepository = questionRepository;
        this.optionRepository = optionRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public List<QuestionResponseDto> generateAndSaveQuestions(
            Long examId,
            AiQuestionGenerateRequestDto requestDto,
            String teacherUsername) {

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + examId));

        if (exam.getCreatedBy() == null || !exam.getCreatedBy().getUsername().equals(teacherUsername)) {
            throw new BadRequestException("You can only generate questions for exams you created.");
        }

        int count = requestDto.getNumberOfQuestions() > 0 ? requestDto.getNumberOfQuestions() : 3;
        int marks = (requestDto.getDefaultMarks() != null && requestDto.getDefaultMarks() > 0) ? requestDto.getDefaultMarks() : 5;
        String difficulty = (requestDto.getDifficulty() != null) ? requestDto.getDifficulty() : "Medium";

        String prompt = String.format("""
                You are a senior computer science teacher.
                Generate exactly %d multiple-choice questions on the topic '%s' with difficulty '%s'.
                Each question must have exactly 4 options, and exactly one option must have isCorrect set to true.
                
                Respond ONLY with a valid JSON array matching this exact format, with NO markdown formatting, NO backticks, and NO extra text:
                [
                  {
                    "questionText": "Question string here?",
                    "options": [
                      {"optionText": "Choice A", "isCorrect": true},
                      {"optionText": "Choice B", "isCorrect": false},
                      {"optionText": "Choice C", "isCorrect": false},
                      {"optionText": "Choice D", "isCorrect": false}
                    ]
                  }
                ]
                """, count, requestDto.getTopic(), difficulty);

        String rawResponse = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        if (rawResponse == null || rawResponse.isBlank()) {
            throw new BadRequestException("AI returned an empty response. Please try again.");
        }

        // Clean any markdown code blocks if the LLM includes them
        String cleanJson = rawResponse.trim();
        if (cleanJson.startsWith("```json")) {
            cleanJson = cleanJson.substring(7);
        } else if (cleanJson.startsWith("```")) {
            cleanJson = cleanJson.substring(3);
        }
        if (cleanJson.endsWith("```")) {
            cleanJson = cleanJson.substring(0, cleanJson.length() - 3);
        }
        cleanJson = cleanJson.trim();

        List<AiGeneratedQuestionDto> aiQuestions;
        try {
            aiQuestions = objectMapper.readValue(cleanJson, new TypeReference<List<AiGeneratedQuestionDto>>() {});
        } catch (Exception e) {
            throw new BadRequestException("Failed to parse AI response into questions: " + e.getMessage());
        }

        List<QuestionResponseDto> responseList = new ArrayList<>();

        for (AiGeneratedQuestionDto aiQ : aiQuestions) {
            Question question = new Question();
            question.setContent(aiQ.getQuestionText());
            question.setMarks(marks);
            question.setExam(exam);

            Question savedQuestion = questionRepository.save(question);

            List<Option> savedOptions = new ArrayList<>();
            if (aiQ.getOptions() != null) {
                for (AiGeneratedOptionDto aiOpt : aiQ.getOptions()) {
                    Option opt = new Option();
                    opt.setOptionText(aiOpt.getOptionText());
                    opt.setCorrect(aiOpt.isCorrect());
                    opt.setQuestion(savedQuestion);
                    savedOptions.add(optionRepository.save(opt));
                }
            }

            List<OptionResponseDto> optionDtos = savedOptions.stream()
                    .map(o -> new OptionResponseDto(o.getId(), o.getOptionText(), o.isCorrect()))
                    .collect(Collectors.toList());

            responseList.add(new QuestionResponseDto(
                    savedQuestion.getId(),
                    savedQuestion.getContent(),
                    savedQuestion.getMarks(),
                    exam.getId(),
                    optionDtos
            ));
        }

        return responseList;
    }
}