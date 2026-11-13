package com.exam.service;

import com.exam.dto.AnswerSubmissionDto;
import com.exam.dto.ExamResultResponseDto;
import com.exam.dto.ExamSubmissionDto;
import com.exam.entity.Exam;
import com.exam.entity.Option;
import com.exam.entity.Question;
import com.exam.entity.Role;
import com.exam.entity.StudentResult;
import com.exam.entity.User;
import com.exam.exception.BadRequestException;
import com.exam.exception.ResourceNotFoundException;
import com.exam.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExamSubmissionServiceImplTest {

    @Mock
    private ExamRepository examRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StudentResultRepository studentResultRepository;

    @Mock
    private StudentAnswerRepository studentAnswerRepository;

    @InjectMocks
    private ExamSubmissionServiceImpl examSubmissionService;

    private User studentUser;
    private Exam publishedExam;

    @BeforeEach
    void setUp() {
        Role studentRole = new Role();
        studentRole.setId(1L);
        studentRole.setName("ROLE_STUDENT");

        studentUser = new User();
        studentUser.setId(10L);
        studentUser.setUsername("test_student");
        studentUser.setFullName("Test Student");
        studentUser.setRoles(Set.of(studentRole));

        publishedExam = new Exam();
        publishedExam.setId(100L);
        publishedExam.setTitle("Core Java Assessment");
        publishedExam.setPublished(true);
        publishedExam.setTotalMarks(20);
        publishedExam.setPassMarks(10);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when exam does not exist")
    void submitExam_ExamNotFound_ThrowsException() {
        Long invalidExamId = 999L;
        ExamSubmissionDto dto = new ExamSubmissionDto();

        when(examRepository.findById(invalidExamId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> examSubmissionService.submitExam(invalidExamId, dto, "test_student")
        );

        assertEquals("Exam not found with id: 999", exception.getMessage());
        verify(examRepository, times(1)).findById(invalidExamId);
        verifyNoInteractions(studentResultRepository);
    }

    @Test
    @DisplayName("Should throw BadRequestException when exam is not published")
    void submitExam_UnpublishedExam_ThrowsBadRequestException() {
        publishedExam.setPublished(false);
        ExamSubmissionDto dto = new ExamSubmissionDto();

        when(examRepository.findById(100L)).thenReturn(Optional.of(publishedExam));

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> examSubmissionService.submitExam(100L, dto, "test_student")
        );

        assertEquals("Cannot submit answers for an unpublished exam.", exception.getMessage());
        verify(examRepository, times(1)).findById(100L);
        verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("Should prevent duplicate submissions by throwing BadRequestException")
    void submitExam_DuplicateSubmission_ThrowsBadRequestException() {
        ExamSubmissionDto dto = new ExamSubmissionDto();

        when(examRepository.findById(100L)).thenReturn(Optional.of(publishedExam));
        when(userRepository.findByUsername("test_student")).thenReturn(Optional.of(studentUser));
        when(studentResultRepository.existsByExamIdAndStudentId(100L, 10L)).thenReturn(true);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> examSubmissionService.submitExam(100L, dto, "test_student")
        );

        assertEquals("You have already submitted this exam. Multiple attempts are not permitted.", exception.getMessage());
        verify(studentResultRepository, times(1)).existsByExamIdAndStudentId(100L, 10L);
        verify(studentResultRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("Should successfully grade exam, calculate marks, and persist student answers")
    void submitExam_Success_CalculatesMarksAndPassStatus() {
        // Explicitly set passMarks to 5 for this test scenario
        publishedExam.setPassMarks(5);

        // 1. Prepare Question 1 (5 marks) with Options
        Option q1Opt1 = new Option();
        q1Opt1.setId(201L);
        q1Opt1.setOptionText("Java is platform independent");
        q1Opt1.setCorrect(true);

        Option q1Opt2 = new Option();
        q1Opt2.setId(202L);
        q1Opt2.setOptionText("Java is platform dependent");
        q1Opt2.setCorrect(false);

        Question q1 = new Question();
        q1.setId(101L);
        q1.setContent("Why is Java portable?");
        q1.setMarks(5);
        q1.setOptions(List.of(q1Opt1, q1Opt2));

        // 2. Prepare Question 2 (5 marks) with Options
        Option q2Opt1 = new Option();
        q2Opt1.setId(203L);
        q2Opt1.setOptionText("8");
        q2Opt1.setCorrect(true);

        Option q2Opt2 = new Option();
        q2Opt2.setId(204L);
        q2Opt2.setOptionText("4");
        q2Opt2.setCorrect(false);

        Question q2 = new Question();
        q2.setId(102L);
        q2.setContent("Number of primitive types in Java?");
        q2.setMarks(5);
        q2.setOptions(List.of(q2Opt1, q2Opt2));

        // 3. Prepare Student Submissions (Q1: Correct, Q2: Incorrect)
        AnswerSubmissionDto answer1 = new AnswerSubmissionDto();
        answer1.setQuestionId(101L);
        answer1.setSelectedOptionId(201L); // Correct choice

        AnswerSubmissionDto answer2 = new AnswerSubmissionDto();
        answer2.setQuestionId(102L);
        answer2.setSelectedOptionId(204L); // Incorrect choice

        ExamSubmissionDto submissionDto = new ExamSubmissionDto();
        submissionDto.setAnswers(List.of(answer1, answer2));

        // 4. Stub Mock Repositories
        when(examRepository.findById(100L)).thenReturn(Optional.of(publishedExam));
        when(userRepository.findByUsername("test_student")).thenReturn(Optional.of(studentUser));
        when(studentResultRepository.existsByExamIdAndStudentId(100L, 10L)).thenReturn(false);
        when(questionRepository.findByExamId(100L)).thenReturn(List.of(q1, q2));

        when(studentResultRepository.save(any(StudentResult.class))).thenAnswer(invocation -> {
            StudentResult saved = invocation.getArgument(0);
            saved.setId(500L);
            return saved;
        });

        // 5. Execute Method Under Test
        ExamResultResponseDto response = examSubmissionService.submitExam(100L, submissionDto, "test_student");

        // 6. Assertions
        assertNotNull(response);
        assertEquals(500L, response.getResultId());
        assertEquals(2, response.getTotalQuestions());
        assertEquals(1, response.getCorrectAnswers());
        assertEquals(1, response.getWrongAnswers());
        assertEquals(5, response.getMarksObtained());
        assertTrue(response.isPassed()); // Now 5 >= 5 evaluates to true

        // Verify repository interactions
        verify(studentAnswerRepository, times(1)).saveAll(anyList());
        verify(studentResultRepository, times(1)).save(any(StudentResult.class));
    }
}