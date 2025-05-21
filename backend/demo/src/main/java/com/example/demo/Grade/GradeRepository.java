package com.example.demo.Grade;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.Assignment.Assignment;
import com.example.demo.Student.Student;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {

    Optional<Grade> findByStudentAndAssignment(Student student, Assignment assignment);
    // You can add custom query methods here if needed
}
