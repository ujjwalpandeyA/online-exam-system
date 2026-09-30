package com.exam.service;

import com.exam.dto.ExamRequestDto;
import com.exam.dto.ExamResponseDto;
import com.exam.entity.Exam;
import com.exam.entity.User;
import com.exam.exception.BadRequestException;
import com.exam.exception.ResourceNotFoundException;
import com.exam.repository.ExamRepository;
import com.exam.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExamServiceImpl implements ExamService {

    private final ExamRepository examRepository;
    private final UserRepository userRepository;

    public ExamServiceImpl(ExamRepository examRepository, UserRepository userRepository) {
        this.examRepository = examRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public ExamResponseDto createExam(ExamRequestDto requestDto, String username) {
        // 1. Fetch authenticated Teacher directly by username from JWT (IDOR protection)
        User teacher = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        // 2. Verify user has TEACHER role
        boolean isTeacher = teacher.getRoles().stream()
                .anyMatch(r -> r.getName().equals("ROLE_TEACHER"));
        if (!isTeacher) {
            throw new BadRequestException("Only teachers can create exams.");
        }

        // 3. Map DTO to Exam Entity
        Exam exam = new Exam();
        exam.setTitle(requestDto.getTitle());
        exam.setDescription(requestDto.getDescription());
        exam.setTotalMarks(requestDto.getTotalMarks());
        exam.setPassMarks(requestDto.getPassMarks());
        exam.setDurationMinutes(requestDto.getDurationMinutes());
        exam.setPublished(false);

        // Uses setCreatedBy because the entity property is createdBy (matches createdByTeacher in DTO)
        exam.setCreatedBy(teacher);

        Exam savedExam = examRepository.save(exam);

        // Fixed method name from mapToDto to mapToResponseDto
        return mapToResponseDto(savedExam);
    }

    @Override
    @Transactional(readOnly = true)
    public ExamResponseDto getExamById(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + examId));
        return mapToResponseDto(exam);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamResponseDto> getAllPublishedExams() {
        return examRepository.findAll().stream()
                .filter(Exam::isPublished)
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamResponseDto> getExamsByTeacher(Long teacherId) {
        return examRepository.findAll().stream()
                .filter(exam -> exam.getCreatedBy() != null && exam.getCreatedBy().getId().equals(teacherId))
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ExamResponseDto publishExam(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + examId));

        exam.setPublished(true);
        Exam updatedExam = examRepository.save(exam);
        return mapToResponseDto(updatedExam);
    }

    private ExamResponseDto mapToResponseDto(Exam exam) {
        String teacherName = (exam.getCreatedBy() != null) ? exam.getCreatedBy().getFullName() : null;
        return new ExamResponseDto(
                exam.getId(),
                exam.getTitle(),
                exam.getDescription(),
                exam.getTotalMarks(),
                exam.getPassMarks(),
                exam.getDurationMinutes(),
                exam.isPublished(),
                teacherName
        );
    }
}