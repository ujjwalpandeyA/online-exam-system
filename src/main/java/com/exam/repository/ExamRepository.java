package com.exam.repository;

import com.exam.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {

    // Fetch all active exams visible to students
    List<Exam> findByPublishedTrue();

    // Fetch all exams created by a specific teacher
    List<Exam> findByCreatedById(Long teacherId);
}