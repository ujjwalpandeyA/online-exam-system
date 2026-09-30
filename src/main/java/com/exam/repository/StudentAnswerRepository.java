package com.exam.repository;

import com.exam.entity.StudentAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentAnswerRepository extends JpaRepository<StudentAnswer, Long> {

    // Fetch individual submitted answers for a particular result
    List<StudentAnswer> findByStudentResultId(Long studentResultId);
}