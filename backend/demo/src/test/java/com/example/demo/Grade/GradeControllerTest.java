package com.example.demo.Grade;

import com.example.demo.Assignment.Assignment;
import com.example.demo.Assignment.AssignmentRepository;
import com.example.demo.Student.Student;
import com.example.demo.Student.StudentRepository;
import com.example.demo.Subject.Subject;
import com.example.demo.Subject.SubjectRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GradeController.class)
public class GradeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GradeRepository gradeRepository;

    @MockBean
    private StudentRepository studentRepository;

    @MockBean
    private SubjectRepository subjectRepository;

    @MockBean
    private AssignmentRepository assignmentRepository;

    private Grade grade;
    private GradeDTO gradeDTO;
    private Student student;
    private Subject subject;
    private Assignment assignment;

    @BeforeEach
    void setUp() {
        student = new Student();
        student.setId(1L);
        // Assuming Student has firstName or similar
        try {
            student.getClass().getMethod("setFirstName", String.class).invoke(student, "John");
        } catch (Exception e) {
            throw new RuntimeException("Failed to set student first name", e);
        }

        subject = new Subject();
        subject.setId(1L);
        subject.setName("Math");

        assignment = new Assignment();
        assignment.setId(1L);
        // Assuming Assignment has title
        try {
            assignment.getClass().getMethod("setTitle", String.class).invoke(assignment, "Math Assignment");
        } catch (Exception e) {
            throw new RuntimeException("Failed to set assignment title", e);
        }

        grade = new Grade();
        grade.setId(1L);
        grade.setScore(90.0);
        grade.setStudent(student);
        grade.setSubject(subject);
        grade.setAssignment(assignment);

        gradeDTO = new GradeDTO();
        gradeDTO.setScore(90.0);
        gradeDTO.setStudentId(1L);
        gradeDTO.setSubjectId(1L);
        gradeDTO.setAssignmentId(1L);
    }

    @Test
    void testCreateGrade_Success() throws Exception {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        when(assignmentRepository.findById(1L)).thenReturn(Optional.of(assignment));
        when(gradeRepository.save(any(Grade.class))).thenReturn(grade);

        String gradeJson = objectMapper.writeValueAsString(gradeDTO);

        mockMvc.perform(post("/api/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gradeJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.score").value(90));

        assertNotNull(gradeRepository.save(grade));
    }

    @Test
    void testCreateGrade_NotFound() throws Exception {
        when(studentRepository.findById(1L)).thenReturn(Optional.empty());

        String gradeJson = objectMapper.writeValueAsString(gradeDTO);

        mockMvc.perform(post("/api/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gradeJson))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateGrade_Success() throws Exception {
        when(gradeRepository.existsById(1L)).thenReturn(true);
        when(gradeRepository.findById(1L)).thenReturn(Optional.of(grade));
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        when(assignmentRepository.findById(1L)).thenReturn(Optional.of(assignment));
        when(gradeRepository.save(any(Grade.class))).thenReturn(grade);

        String gradeJson = objectMapper.writeValueAsString(gradeDTO);

        mockMvc.perform(put("/api/grades/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gradeJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.score").value(90));

        assertEquals(90, gradeRepository.save(grade).getScore());
    }

    @Test
    void testUpdateGrade_NotFound() throws Exception {
        when(gradeRepository.existsById(1L)).thenReturn(false);

        String gradeJson = objectMapper.writeValueAsString(gradeDTO);

        mockMvc.perform(put("/api/grades/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gradeJson))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllGrades() throws Exception {
        when(gradeRepository.findAll()).thenReturn(Arrays.asList(grade));

        mockMvc.perform(get("/api/grades")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].score").value(90));

        assertFalse(gradeRepository.findAll().isEmpty());
    }

    @Test
    void testGetGradeById_Success() throws Exception {
        when(gradeRepository.findById(1L)).thenReturn(Optional.of(grade));

        mockMvc.perform(get("/api/grades/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.score").value(90));

        assertTrue(gradeRepository.findById(1L).isPresent());
    }

    @Test
    void testGetGradeById_NotFound() throws Exception {
        when(gradeRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/grades/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteGrade_Success() throws Exception {
        when(gradeRepository.existsById(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/grades/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        assertTrue(gradeRepository.existsById(1L));
    }

    @Test
    void testDeleteGrade_NotFound() throws Exception {
        when(gradeRepository.existsById(1L)).thenReturn(false);

        mockMvc.perform(delete("/api/grades/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}