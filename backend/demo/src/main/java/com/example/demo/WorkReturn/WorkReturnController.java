package com.example.demo.WorkReturn;

import com.example.demo.Student.Student;
import com.example.demo.Student.StudentRepository;
import com.example.demo.Assignment.Assignment;
import com.example.demo.Assignment.AssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/workreturns")
public class WorkReturnController {

    private static final Logger LOGGER = Logger.getLogger(WorkReturnController.class.getName());

    @Value("${file.upload-dir:D:/AzureDevopsScholarhub/ScholarHub/backend/Uploads/}")
    private String UPLOAD_DIR;

    @Autowired
    private WorkReturnRepository workReturnRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createWorkReturn(
            @RequestParam("assignmentId") Long assignmentId,
            @RequestParam("studentId") Long studentId,
            @RequestParam("file") MultipartFile file) {
        try {
            // Validate input parameters
            if (assignmentId == null || studentId == null) {
                LOGGER.warning("[DEBUG] POST /api/workreturns - Missing assignmentId or studentId");
                return ResponseEntity.badRequest().body(createErrorResponse("Assignment ID and Student ID are required"));
            }

            // Validate file
            if (file == null || file.isEmpty()) {
                LOGGER.warning("[DEBUG] POST /api/workreturns - Empty file uploaded");
                return ResponseEntity.badRequest().body(createErrorResponse("File is empty or missing"));
            }

            String fileName = file.getOriginalFilename();
            if (fileName == null || !(fileName.toLowerCase().endsWith(".pdf") || 
                                      fileName.toLowerCase().endsWith(".doc") || 
                                      fileName.toLowerCase().endsWith(".docx"))) {
                LOGGER.warning("[DEBUG] POST /api/workreturns - Invalid file type: " + fileName);
                return ResponseEntity.badRequest().body(createErrorResponse("Only PDF, DOC, DOCX files are allowed"));
            }

            // Fetch Student and Assignment entities
            Optional<Student> studentOpt = studentRepository.findById(studentId);
            if (!studentOpt.isPresent()) {
                LOGGER.warning("[DEBUG] POST /api/workreturns - Student not found: " + studentId);
                return ResponseEntity.badRequest().body(createErrorResponse("Student not found"));
            }
            Student student = studentOpt.get();

            Optional<Assignment> assignmentOpt = assignmentRepository.findById(assignmentId);
            if (!assignmentOpt.isPresent()) {
                LOGGER.warning("[DEBUG] POST /api/workreturns - Assignment not found: " + assignmentId);
                return ResponseEntity.badRequest().body(createErrorResponse("Assignment not found"));
            }
            Assignment assignment = assignmentOpt.get();

            // Save file
            String uniqueFileName = UUID.randomUUID() + "_" + fileName;
            File dest = new File(UPLOAD_DIR, uniqueFileName);
            try {
                dest.getParentFile().mkdirs(); // Create Uploads directory if it doesn't exist
                LOGGER.info("[DEBUG] POST /api/workreturns - Saving file to: " + dest.getAbsolutePath());
                file.transferTo(dest);
            } catch (IOException e) {
                LOGGER.severe("[DEBUG] POST /api/workreturns - Failed to save file: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(createErrorResponse("Failed to save file: " + e.getMessage()));
            }

            // Create WorkReturn entity
            WorkReturn workReturn = new WorkReturn();
            workReturn.setFilePath("/Uploads/" + uniqueFileName);
            workReturn.setStudent(student);
            workReturn.setAssignment(assignment);

            // Save to database
            LOGGER.info("[DEBUG] POST /api/workreturns - Saving WorkReturn for assignmentId: " + assignmentId);
            WorkReturn savedWorkReturn;
            try {
                savedWorkReturn = workReturnRepository.save(workReturn);
                LOGGER.info("[DEBUG] POST /api/workreturns - Saved WorkReturn: " + savedWorkReturn.getId());
            } catch (Exception e) {
                LOGGER.severe("[DEBUG] POST /api/workreturns - Database save failed: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(createErrorResponse("Failed to save WorkReturn to database: " + e.getMessage()));
            }

            // Update assignment status
            assignment.setStatus("Submitted");
            try {
                assignmentRepository.save(assignment);
                LOGGER.info("[DEBUG] POST /api/workreturns - Updated assignment status to Submitted: " + assignmentId);
            } catch (Exception e) {
                LOGGER.severe("[DEBUG] POST /api/workreturns - Failed to update assignment status: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(createErrorResponse("Failed to update assignment status: " + e.getMessage()));
            }

            // Return JSON with id and fileUrl
            Map<String, Object> response = new HashMap<>();
            response.put("id", savedWorkReturn.getId());
            response.put("fileUrl", savedWorkReturn.getFilePath());
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            LOGGER.severe("[DEBUG] POST /api/workreturns - Unexpected error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Unexpected error processing WorkReturn: " + e.getMessage()));
        }
    }

    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("message", message);
        return error;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<WorkReturn> getAllWorkReturns() {
        LOGGER.info("[DEBUG] GET /api/workreturns - Fetching all WorkReturns");
        return workReturnRepository.findAll();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getWorkReturnById(@PathVariable Long id) {
        LOGGER.info("[DEBUG] GET /api/workreturns/" + id + " - Fetching WorkReturn");
        Optional<WorkReturn> workReturn = workReturnRepository.findById(id);
        if (workReturn.isPresent()) {
            LOGGER.info("[DEBUG] GET /api/workreturns/" + id + " - Found WorkReturn");
            return new ResponseEntity<>(workReturn.get(), HttpStatus.OK);
        } else {
            LOGGER.warning("[DEBUG] GET /api/workreturns/" + id + " - WorkReturn not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createErrorResponse("WorkReturn not found"));
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateWorkReturn(@PathVariable Long id, @RequestBody WorkReturn updatedWorkReturn) {
        LOGGER.info("[DEBUG] PUT /api/workreturns/" + id + " - Updating WorkReturn");
        Optional<WorkReturn> workReturn = workReturnRepository.findById(id);
        if (workReturn.isPresent()) {
            updatedWorkReturn.setId(id);
            WorkReturn savedWorkReturn = workReturnRepository.save(updatedWorkReturn);
            LOGGER.info("[DEBUG] PUT /api/workreturns/" + id + " - Updated WorkReturn");
            return new ResponseEntity<>(savedWorkReturn, HttpStatus.OK);
        } else {
            LOGGER.warning("[DEBUG] PUT /api/workreturns/" + id + " - WorkReturn not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createErrorResponse("WorkReturn not found"));
        }
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deleteWorkReturn(@PathVariable Long id) {
        LOGGER.info("[DEBUG] DELETE /api/workreturns/" + id + " - Deleting WorkReturn");
        if (workReturnRepository.existsById(id)) {
            workReturnRepository.deleteById(id);
            LOGGER.info("[DEBUG] DELETE /api/workreturns/" + id + " - Deleted WorkReturn");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            LOGGER.warning("[DEBUG] DELETE /api/workreturns/" + id + " - WorkReturn not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createErrorResponse("WorkReturn not found"));
        }
    }
}