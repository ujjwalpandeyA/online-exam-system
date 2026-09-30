package com.exam.service;

import com.exam.dto.AnswerSubmissionDto;
import com.exam.dto.ExamResultResponseDto;
import com.exam.dto.ExamSubmissionDto;
import com.exam.entity.Exam;
import com.exam.entity.Question;
import com.exam.entity.StudentResult;
import com.exam.entity.User;
import com.exam.exception.BadRequestException;
import com.exam.exception.ResourceNotFoundException;
import com.exam.repository.ExamRepository;
import com.exam.repository.QuestionRepository;
import com.exam.repository.StudentResultRepository;
import com.exam.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExamSubmissionServiceImpl implements ExamSubmissionService {

    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final StudentResultRepository studentResultRepository;

    public ExamSubmissionServiceImpl(
            ExamRepository examRepository,
            QuestionRepository questionRepository,
            UserRepository userRepository,
            StudentResultRepository studentResultRepository) {
        this.examRepository = examRepository;
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.studentResultRepository = studentResultRepository;
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

        if (submissionDto.getAnswers() != null) {
            for (AnswerSubmissionDto answer : submissionDto.getAnswers()) {
                Question question = questionMap.get(answer.getQuestionId());
                if (question != null && question.getOptions() != null) {
                    boolean isCorrect = question.getOptions().stream()
                            .anyMatch(opt -> opt.isCorrect() && opt.getId().equals(answer.getSelectedOptionId()));

                    if (isCorrect) {
                        correctAnswers++;
                        totalCalculatedMarks += (question.getMarks() != null ? question.getMarks() : 0);
                    }
                }
            }
        }

        int totalQuestions = examQuestions.size();
        int wrongAnswers = totalQuestions - correctAnswers;
        boolean passed = totalCalculatedMarks >= exam.getPassMarks();

        StudentResult result = new StudentResult();
        result.setExam(exam);
        result.setStudent(student);
        result.setMarksObtained(totalCalculatedMarks);
        result.setTotalMarks(exam.getTotalMarks());
        result.setPassed(passed);
        result.setSubmittedAt(LocalDateTime.now());

        StudentResult savedResult = studentResultRepository.save(result);

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