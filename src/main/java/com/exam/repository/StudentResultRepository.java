package com.exam.repository;

import com.exam.entity.StudentResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentResultRepository extends JpaRepository<StudentResult, Long> {

    // Check if the student has already taken this exam
    boolean existsByExamIdAndStudentId(Long examId, Long studentId);

    List<StudentResult> findByStudentId(Long studentId);

    List<StudentResult> findByStudentUsername(String username);

    List<StudentResult> findByExamId(Long examId);
}