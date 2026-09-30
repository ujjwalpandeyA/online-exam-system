package com.exam.repository;

import com.exam.entity.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OptionRepository extends JpaRepository<Option, Long> {

    // Fetch all 4 multiple-choice options belonging to a specific question
    List<Option> findByQuestionId(Long questionId);
}