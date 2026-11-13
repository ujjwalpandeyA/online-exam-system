package com.exam.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "student_answers")
public class StudentAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_result_id", nullable = false)
    private StudentResult studentResult;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_option_id")
    private Option selectedOption;

    @Column(name = "is_correct")
    private boolean isCorrect;

    @Column(name = "marks_awarded")
    private Integer marksAwarded;

    public StudentAnswer() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public StudentResult getStudentResult() { return studentResult; }
    public void setStudentResult(StudentResult studentResult) { this.studentResult = studentResult; }

    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { this.question = question; }

    public Option getSelectedOption() { return selectedOption; }
    public void setSelectedOption(Option selectedOption) { this.selectedOption = selectedOption; }

    public boolean isCorrect() { return isCorrect; }
    public void setCorrect(boolean correct) { isCorrect = correct; }

    public Integer getMarksAwarded() { return marksAwarded; }
    public void setMarksAwarded(Integer marksAwarded) { this.marksAwarded = marksAwarded; }
}