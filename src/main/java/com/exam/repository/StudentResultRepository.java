package com.exam.repository;

import com.exam.entity.StudentResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentResultRepository extends JpaRepository<StudentResult, Long> {

    boolean existsByExamIdAndStudentId(Long examId, Long studentId);

    List<StudentResult> findByStudentId(Long studentId);

    List<StudentResult> findByStudentUsername(String username);

    List<StudentResult> findByExamId(Long examId);

    // Leaderboard query: highest marks first; ties broken by earliest submission
    List<StudentResult> findByExamIdOrderByMarksObtainedDescSubmittedAtAsc(Long examId);
}