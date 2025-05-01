package com.example.demo.WorkReturn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/workreturns")
public class WorkReturnController {

    @Autowired
    private WorkReturnRepository workReturnRepository;

    // Create a new WorkReturn
    @PostMapping
    public ResponseEntity<WorkReturn> createWorkReturn(@RequestBody WorkReturn workReturn) {
        WorkReturn savedWorkReturn = workReturnRepository.save(workReturn);
        return new ResponseEntity<>(savedWorkReturn, HttpStatus.CREATED);
    }

    // Get all WorkReturns
    @GetMapping
    public List<WorkReturn> getAllWorkReturns() {
        return workReturnRepository.findAll();
    }

    // Get WorkReturn by ID
    @GetMapping("/{id}")
    public ResponseEntity<WorkReturn> getWorkReturnById(@PathVariable Long id) {
        Optional<WorkReturn> workReturn = workReturnRepository.findById(id);
        if (workReturn.isPresent()) {
            return new ResponseEntity<>(workReturn.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Update WorkReturn by ID
    @PutMapping("/{id}")
    public ResponseEntity<WorkReturn> updateWorkReturn(@PathVariable Long id, @RequestBody WorkReturn updatedWorkReturn) {
        Optional<WorkReturn> workReturn = workReturnRepository.findById(id);
        if (workReturn.isPresent()) {
            updatedWorkReturn.setId(id); // Set the ID to ensure the correct work return is updated
            WorkReturn savedWorkReturn = workReturnRepository.save(updatedWorkReturn);
            return new ResponseEntity<>(savedWorkReturn, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Delete WorkReturn by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkReturn(@PathVariable Long id) {
        Optional<WorkReturn> workReturn = workReturnRepository.findById(id);
        if (workReturn.isPresent()) {
            workReturnRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
