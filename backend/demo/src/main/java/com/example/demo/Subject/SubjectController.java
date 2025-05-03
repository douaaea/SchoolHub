package com.example.demo.Subject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    @Autowired
    private SubjectRepository subjectRepository;

    // Create a new Subject
    @PostMapping
    public ResponseEntity<Subject> createSubject(@RequestBody Subject subject) {
        Subject savedSubject = subjectRepository.save(subject);
        return new ResponseEntity<>(savedSubject, HttpStatus.CREATED);
    }

       private SubjectDTO convertToDTO(Subject subject) {
        String levelName = subject.getLevel() != null ? subject.getLevel().getName() : "N/A";
        return new SubjectDTO(subject.getId(), subject.getName(), levelName);
    }

    // Get all Subjects
    @GetMapping
    public List<SubjectDTO> getAllSubjects() {
        List<Subject> subjects = subjectRepository.findAll();
        List<SubjectDTO> subjectDTOs = new ArrayList<>();
        for (Subject subject : subjects) {
            subjectDTOs.add(convertToDTO(subject));
        }
        return subjectDTOs;
    }
    

    // Get Subject by ID
    @GetMapping("/{id}")
    public ResponseEntity<Subject> getSubjectById(@PathVariable Long id) {
        Optional<Subject> subject = subjectRepository.findById(id);
        if (subject.isPresent()) {
            return new ResponseEntity<>(subject.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Update Subject by ID
    @PutMapping("/{id}")
    public ResponseEntity<Subject> updateSubject(@PathVariable Long id, @RequestBody Subject updatedSubject) {
        Optional<Subject> subject = subjectRepository.findById(id);
        if (subject.isPresent()) {
            updatedSubject.setId(id); // Set the ID to ensure the correct subject is updated
            Subject savedSubject = subjectRepository.save(updatedSubject);
            return new ResponseEntity<>(savedSubject, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Delete Subject by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubject(@PathVariable Long id) {
        Optional<Subject> subject = subjectRepository.findById(id);
        if (subject.isPresent()) {
            subjectRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
