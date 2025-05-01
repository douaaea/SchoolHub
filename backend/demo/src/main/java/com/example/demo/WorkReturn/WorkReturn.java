package com.example.demo.WorkReturn;

import com.example.demo.Student.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.example.demo.Assignment.Assignment;

import jakarta.persistence.*;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class WorkReturn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filePath; // Path to the uploaded file

    @ManyToOne
    @JoinColumn(name = "student_id")
    @JsonIgnore  // Ignore the student field to avoid recursion
    private Student student;

    @ManyToOne
    @JoinColumn(name = "assignment_id")
    @JsonIgnore  // Ignore the assignment field to avoid recursion
    private Assignment assignment;

    public WorkReturn() {}

    public WorkReturn(String filePath, Student student, Assignment assignment) {
        this.filePath = filePath;
        this.student = student;
        this.assignment = assignment;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id; 
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Assignment getAssignment() {
        return assignment;
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }
}
