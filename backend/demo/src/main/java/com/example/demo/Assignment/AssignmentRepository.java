package com.example.demo.Assignment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    // You can define custom query methods here if needed, for example:
    // List<Assignment> findBySubject(Subject subject);
}
