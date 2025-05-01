package com.example.demo.Teacher;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    boolean existsByEmail(String email);
    Teacher findByEmailAndPassword(String email, String password); // For login or testing
}
