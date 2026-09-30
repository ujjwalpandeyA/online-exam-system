package com.exam.service;

import com.exam.dto.OptionResponseDto;
import com.exam.dto.QuestionRequestDto;
import com.exam.dto.QuestionResponseDto;
import com.exam.dto.StudentOptionResponseDto;
import com.exam.dto.StudentQuestionResponseDto;
import com.exam.entity.Exam;
import com.exam.entity.Option;
import com.exam.entity.Question;
import com.exam.exception.BadRequestException;
import com.exam.exception.ResourceNotFoundException;
import com.exam.repository.ExamRepository;
import com.exam.repository.QuestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final ExamRepository examRepository;

    public QuestionServiceImpl(QuestionRepository questionRepository, ExamRepository examRepository) {
        this.questionRepository = questionRepository;
        this.examRepository = examRepository;
    }

    @Override
    @Transactional
    public QuestionResponseDto addQuestionToExam(Long examId, QuestionRequestDto dto) {
        // 1. Validate Exam exists
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + examId));

        // 2. Business Validation: Question must have at least 2 options
        if (dto.getOptions() == null || dto.getOptions().size() < 2) {
            throw new BadRequestException("A question must have at least 2 options.");
        }

        // 3. Business Validation: Exactly one correct option
        long correctCount = dto.getOptions().stream()
                .filter(opt -> opt.isCorrect())
                .count();

        if (correctCount != 1) {
            throw new BadRequestException("A question must have exactly one correct option. Found: " + correctCount);
        }

        // 4. Map DTO to Question Entity
        Question question = new Question();
        question.setContent(dto.getContent());
        question.setMarks(dto.getMarks());
        question.setExam(exam);

        // 5. Map Options and link bidirectional relationship
        List<Option> options = dto.getOptions().stream().map(optDto -> {
            Option option = new Option();
            option.setOptionText(optDto.getOptionText());
            option.setCorrect(optDto.isCorrect());
            option.setQuestion(question); // Maintain foreign key reference
            return option;
        }).collect(Collectors.toList());

        question.setOptions(options);

        // 6. Save Question (CascadeType.ALL persists options automatically)
        Question savedQuestion = questionRepository.save(question);

        return mapToResponseDto(savedQuestion);
    }
   
           
    @Override
    public List<StudentQuestionResponseDto> getQuestionsForStudent(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + examId));

        if (!exam.isPublished()) {
            throw new BadRequestException("This exam has not been published yet.");
        }

        List<Question> questions = questionRepository.findByExamId(examId);

        return questions.stream().map(question -> {
            List<StudentOptionResponseDto> optionDtos = question.getOptions().stream()
                    .map(option -> new StudentOptionResponseDto(option.getId(), option.getOptionText()))
                    .collect(Collectors.toList());

            return new StudentQuestionResponseDto(
                    question.getId(),
                    question.getContent(),
                    question.getMarks(),
                    exam.getId(),
                    optionDtos
            );
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponseDto> getQuestionsByExamId(Long examId) {
        if (!examRepository.existsById(examId)) {
            throw new ResourceNotFoundException("Exam not found with id: " + examId);
        }

        return questionRepository.findByExamId(examId)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteQuestion(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + questionId));
        questionRepository.delete(question);
    }

    private QuestionResponseDto mapToResponseDto(Question question) {
        List<OptionResponseDto> optionDtos = question.getOptions().stream()
                .map(opt -> new OptionResponseDto(opt.getId(), opt.getOptionText(), opt.isCorrect()))
                .collect(Collectors.toList());

        return new QuestionResponseDto(
                question.getId(),
                question.getContent(),
                question.getMarks(),
                question.getExam().getId(),
                optionDtos
        );
    }
}