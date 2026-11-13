package com.exam.service;

import com.exam.dto.*;
import com.exam.entity.*;
import com.exam.exception.BadRequestException;
import com.exam.exception.ResourceNotFoundException;
import com.exam.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExamSubmissionServiceImpl implements ExamSubmissionService {

    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final StudentResultRepository studentResultRepository;
    private final StudentAnswerRepository studentAnswerRepository;

    public ExamSubmissionServiceImpl(
            ExamRepository examRepository,
            QuestionRepository questionRepository,
            UserRepository userRepository,
            StudentResultRepository studentResultRepository,
            StudentAnswerRepository studentAnswerRepository) {
        this.examRepository = examRepository;
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.studentResultRepository = studentResultRepository;
        this.studentAnswerRepository = studentAnswerRepository;
    }

    @Override
    @Transactional
    public ExamResultResponseDto submitExam(Long examId, ExamSubmissionDto submissionDto, String username) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + examId));

        if (!exam.isPublished()) {
            throw new BadRequestException("Cannot submit answers for an unpublished exam.");
        }

        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        boolean isStudent = student.getRoles().stream()
                .anyMatch(r -> r.getName().equals("ROLE_STUDENT"));
        if (!isStudent) {
            throw new BadRequestException("Only students can submit exams.");
        }

        // Rule: Prevent duplicate submissions
        if (studentResultRepository.existsByExamIdAndStudentId(examId, student.getId())) {
            throw new BadRequestException("You have already submitted this exam. Multiple attempts are not permitted.");
        }

        List<Question> examQuestions = questionRepository.findByExamId(examId);
        if (examQuestions.isEmpty()) {
            throw new BadRequestException("This exam has no questions.");
        }

        Map<Long, Question> questionMap = examQuestions.stream()
                .collect(Collectors.toMap(Question::getId, q -> q));

        int correctAnswers = 0;
        int totalCalculatedMarks = 0;
        List<StudentAnswer> studentAnswersList = new ArrayList<>();

        if (submissionDto.getAnswers() != null) {
            for (AnswerSubmissionDto answer : submissionDto.getAnswers()) {
                Question question = questionMap.get(answer.getQuestionId());
                if (question != null && question.getOptions() != null) {
                    
                    Option chosenOption = question.getOptions().stream()
                            .filter(opt -> opt.getId().equals(answer.getSelectedOptionId()))
                            .findFirst()
                            .orElse(null);

                    boolean isCorrect = chosenOption != null && chosenOption.isCorrect();
                    int marks = isCorrect ? (question.getMarks() != null ? question.getMarks() : 0) : 0;

                    if (isCorrect) {
                        correctAnswers++;
                        totalCalculatedMarks += marks;
                    }

                    // Map individual student choice
                    StudentAnswer studentAnswer = new StudentAnswer();
                    studentAnswer.setQuestion(question);
                    studentAnswer.setSelectedOption(chosenOption);
                    studentAnswer.setCorrect(isCorrect);
                    studentAnswer.setMarksAwarded(marks);
                    studentAnswersList.add(studentAnswer);
                }
            }
        }

        int totalQuestions = examQuestions.size();
        int wrongAnswers = totalQuestions - correctAnswers;
        boolean passed = totalCalculatedMarks >= exam.getPassMarks();

        // Save Result Entity
        StudentResult result = new StudentResult();
        result.setExam(exam);
        result.setStudent(student);
        result.setMarksObtained(totalCalculatedMarks);
        result.setTotalMarks(exam.getTotalMarks());
        result.setPassed(passed);
        result.setSubmittedAt(LocalDateTime.now());

        StudentResult savedResult = studentResultRepository.save(result);

        // Associate and persist individual answers
        for (StudentAnswer sa : studentAnswersList) {
            sa.setStudentResult(savedResult);
        }
        studentAnswerRepository.saveAll(studentAnswersList);

        return new ExamResultResponseDto(
                savedResult.getId(),
                exam.getTitle(),
                student.getFullName(),
                totalQuestions,
                correctAnswers,
                wrongAnswers,
                exam.getTotalMarks(),
                savedResult.getMarksObtained(),
                exam.getPassMarks(),
                savedResult.isPassed(),
                savedResult.getSubmittedAt()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ExamResultResponseDto getResultById(Long resultId) {
        StudentResult result = studentResultRepository.findById(resultId)
                .orElseThrow(() -> new ResourceNotFoundException("Result not found with id: " + resultId));

        return mapToDto(result);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamResultResponseDto> getResultsByStudent(Long studentId) {
        return studentResultRepository.findByStudentId(studentId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamResultResponseDto> getMyResults(String username) {
        return studentResultRepository.findByStudentUsername(username)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamResultResponseDto> getExamResultsForTeacher(Long examId, String teacherUsername) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + examId));

        if (exam.getCreatedBy() == null || !exam.getCreatedBy().getUsername().equals(teacherUsername)) {
            throw new BadRequestException("You can only view results for exams you created.");
        }

        return studentResultRepository.findByExamId(examId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<LeaderboardEntryDto> getExamLeaderboard(Long examId) {
        if (!examRepository.existsById(examId)) {
            throw new ResourceNotFoundException("Exam not found with id: " + examId);
        }

        List<StudentResult> sortedResults = studentResultRepository
                .findByExamIdOrderByMarksObtainedDescSubmittedAtAsc(examId);

        List<LeaderboardEntryDto> leaderboard = new ArrayList<>();
        int currentRank = 1;

        for (StudentResult result : sortedResults) {
            double percentage = result.getTotalMarks() > 0
                    ? Math.round(((double) result.getMarksObtained() / result.getTotalMarks() * 100.0) * 100.0) / 100.0
                    : 0.0;

            leaderboard.add(new LeaderboardEntryDto(
                    currentRank++,
                    result.getStudent().getId(),
                    result.getStudent().getFullName(),
                    result.getMarksObtained(),
                    result.getTotalMarks(),
                    percentage,
                    result.isPassed(),
                    result.getSubmittedAt()
            ));
        }

        return leaderboard;
    }

    @Override
    @Transactional(readOnly = true)
    public ExamReviewResponseDto getExamReview(Long resultId, String username) {
        StudentResult result = studentResultRepository.findById(resultId)
                .orElseThrow(() -> new ResourceNotFoundException("Result not found with id: " + resultId));

        boolean isStudentOwner = result.getStudent().getUsername().equals(username);
        boolean isExamTeacher = result.getExam().getCreatedBy() != null &&
                result.getExam().getCreatedBy().getUsername().equals(username);

        if (!isStudentOwner && !isExamTeacher) {
            throw new BadRequestException("You are not authorized to view this exam review.");
        }

        List<StudentAnswer> answers = studentAnswerRepository.findByStudentResultId(resultId);

        List<QuestionReviewDto> questionReviews = answers.stream().map(sa -> {
            Question q = sa.getQuestion();
            Option selected = sa.getSelectedOption();
            Option correct = q.getOptions().stream()
                    .filter(Option::isCorrect)
                    .findFirst()
                    .orElse(null);

            return new QuestionReviewDto(
                    q.getId(),
                    q.getContent(),
                    sa.getMarksAwarded(),
                    selected != null ? selected.getId() : null,
                    selected != null ? selected.getOptionText() : "Not Answered",
                    correct != null ? correct.getId() : null,
                    correct != null ? correct.getOptionText() : "N/A",
                    sa.isCorrect()
            );
        }).toList();

        return new ExamReviewResponseDto(
                result.getId(),
                result.getExam().getTitle(),
                result.getStudent().getFullName(),
                result.getTotalMarks(),
                result.getMarksObtained(),
                result.isPassed(),
                result.getSubmittedAt(),
                questionReviews
        );
    }

    private ExamResultResponseDto mapToDto(StudentResult result) {
        return new ExamResultResponseDto(
                result.getId(),
                result.getExam().getTitle(),
                result.getStudent().getFullName(),
                result.getExam().getQuestions() != null ? result.getExam().getQuestions().size() : 0,
                null,
                null,
                result.getTotalMarks(),
                result.getMarksObtained(),
                result.getExam().getPassMarks(),
                result.isPassed(),
                result.getSubmittedAt()
        );
    }
}