package com.exam.dto;

import java.time.LocalDateTime;

public record LeaderboardEntryDto(
    int rank,
    Long studentId,
    String studentName,
    Integer marksObtained,
    Integer totalMarks,
    Double percentage,
    boolean passed,
    LocalDateTime submittedAt
) {}