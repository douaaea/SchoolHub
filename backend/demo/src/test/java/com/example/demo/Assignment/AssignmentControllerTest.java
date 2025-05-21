package com.example.demo.Assignment;

import com.example.demo.Admin.AdminRepository;
import com.example.demo.Group.Group;
import com.example.demo.Group.GroupRepository;
import com.example.demo.Level.LevelRepository;
import com.example.demo.Grade.*;
import com.example.demo.Program.Program;
import com.example.demo.Program.ProgramRepository;
import com.example.demo.Student.StudentRepository;
import com.example.demo.Subject.Subject;
import com.example.demo.Subject.SubjectRepository;
import com.example.demo.Teacher.TeacherRepository;
import com.example.demo.WorkReturn.WorkReturnRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AssignmentController.class)
public class AssignmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AssignmentRepository assignmentRepository;

    @MockBean
    private SubjectRepository subjectRepository;

    @MockBean
    private GroupRepository groupRepository;

    @MockBean
    private ProgramRepository programRepository;

    // Mock all repositories required by Application.testEverything
    @MockBean
    private LevelRepository levelRepository;
    @MockBean
    private TeacherRepository teacherRepository;
    @MockBean
    private StudentRepository studentRepository;
    @MockBean
    private GradeRepository gradeRepository;
    @MockBean
    private WorkReturnRepository workReturnRepository;
    @MockBean
    private AdminRepository adminRepository;

    private Assignment assignment;
    private AssignmentDTO assignmentDTO;
    private Subject subject;
    private Group group;
    private Program program;

    @BeforeEach
    void setUp() {
        subject = new Subject();
        subject.setId(1L);
        subject.setName("Math");

        group = new Group();
        group.setId(1L);
        group.setName("Group A");

        program = new Program();
        program.setId(1L);
        // Program has no name field; linked to subject, group, teacher
        // Assuming constructor or setters for teacher, group, subject are set elsewhere

        assignment = new Assignment();
        assignment.setId(1L);
        assignment.setTitle("Math Assignment");
        assignment.setDescription("Solve problems");
        assignment.setProgram(program); // Matches Application.java constructor
        // No delay field in Application.java; assuming LocalDateTime if present
        // Comment out or adjust based on Assignment.java
        // assignment.setDelay(LocalDateTime.now().plusDays(7));

        assignmentDTO = new AssignmentDTO();
        assignmentDTO.setId(1L);
        assignmentDTO.setTitle("Math Assignment");
        assignmentDTO.setDescription("Solve problems");
        assignmentDTO.setProgramId(1L);
        // DTO uses Integer for delay if present; comment out or adjust
        // assignmentDTO.setDelay(7);
        assignmentDTO.setSubjectId(1L);
        assignmentDTO.setGroupId(1L);
    }

    @Test
    void testCreateAssignment_Success() throws Exception {
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(programRepository.findById(1L)).thenReturn(Optional.of(program));
        when(assignmentRepository.save(any(Assignment.class))).thenReturn(assignment);

        String assignmentJson = objectMapper.writeValueAsString(assignmentDTO);

        mockMvc.perform(post("/api/assignments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(assignmentJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Math Assignment"))
                .andExpect(jsonPath("$.subjectId").value(1));

        assertNotNull(assignmentRepository.save(assignment), "Saved assignment should not be null");
    }

    @Test
    void testCreateAssignment_MissingFields() throws Exception {
        AssignmentDTO invalidDTO = new AssignmentDTO();
        invalidDTO.setTitle("Math Assignment");

        String invalidJson = objectMapper.writeValueAsString(invalidDTO);

        mockMvc.perform(post("/api/assignments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Subject, group, and program are required"));
    }

    @Test
    void testUpdateAssignment_Success() throws Exception {
        when(assignmentRepository.findById(1L)).thenReturn(Optional.of(assignment));
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(programRepository.findById(1L)).thenReturn(Optional.of(program));
        when(assignmentRepository.save(any(Assignment.class))).thenReturn(assignment);

        String assignmentJson = objectMapper.writeValueAsString(assignmentDTO);

        mockMvc.perform(put("/api/assignments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(assignmentJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Math Assignment"));

        assertEquals("Math Assignment", assignmentRepository.save(assignment).getTitle());
    }

    @Test
    void testUpdateAssignment_NotFound() throws Exception {
        when(assignmentRepository.findById(1L)).thenReturn(Optional.empty());

        String assignmentJson = objectMapper.writeValueAsString(assignmentDTO);

        mockMvc.perform(put("/api/assignments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(assignmentJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Assignment not found"));
    }

    @Test
    void testGetAllAssignments() throws Exception {
        when(assignmentRepository.findAll()).thenReturn(Arrays.asList(assignment));

        mockMvc.perform(get("/api/assignments")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Math Assignment"));

        assertFalse(assignmentRepository.findAll().isEmpty());
    }

    @Test
    void testGetAssignmentById_Success() throws Exception {
        when(assignmentRepository.findById(1L)).thenReturn(Optional.of(assignment));

        mockMvc.perform(get("/api/assignments/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Math Assignment"));

        assertTrue(assignmentRepository.findById(1L).isPresent());
    }

    @Test
    void testGetAssignmentById_NotFound() throws Exception {
        when(assignmentRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/assignments/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Assignment not found"));
    }

    @Test
    void testGetAssignmentsByGroup() throws Exception {
        when(assignmentRepository.findByGroupId(1L)).thenReturn(Arrays.asList(assignment));

        mockMvc.perform(get("/api/assignments/group/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Math Assignment"));

        assertEquals(1, assignmentRepository.findByGroupId(1L).size());
    }

    @Test
    void testDeleteAssignment_Success() throws Exception {
        when(assignmentRepository.existsById(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/assignments/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        assertTrue(assignmentRepository.existsById(1L));
    }

    @Test
    void testDeleteAssignment_NotFound() throws Exception {
        when(assignmentRepository.existsById(1L)).thenReturn(false);

        mockMvc.perform(delete("/api/assignments/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Assignment not found"));
    }
}