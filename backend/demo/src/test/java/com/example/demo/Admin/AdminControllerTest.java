package com.example.demo.Admin;

import com.example.demo.Assignment.AssignmentRepository;
import com.example.demo.Grade.GradeRepository;
import com.example.demo.Group.GroupRepository;
import com.example.demo.Level.LevelRepository;
import com.example.demo.Program.ProgramRepository;
import com.example.demo.Student.StudentRepository;
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
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdminRepository adminRepository;

    // Mock all repositories required by Application.testEverything
    @MockBean
    private LevelRepository levelRepository;
    @MockBean
    private SubjectRepository subjectRepository;
    @MockBean
    private TeacherRepository teacherRepository;
    @MockBean
    private GroupRepository groupRepository;
    @MockBean
    private ProgramRepository programRepository;
    @MockBean
    private StudentRepository studentRepository;
    @MockBean
    private AssignmentRepository assignmentRepository;
    @MockBean
    private GradeRepository gradeRepository;
    @MockBean
    private WorkReturnRepository workReturnRepository;

    private Admin admin;

    @BeforeEach
    void setUp() {
        admin = new Admin();
        admin.setId(1L);
        admin.setEmail("admin@example.com");
        admin.setPassword("password123");
        admin.setFirstname("John");
        admin.setLastname("Doe");
        admin.setInstitutionName("ScholarHub");
    }

    @Test
    void testCreateAdmin_Success() throws Exception {
        when(adminRepository.save(any(Admin.class))).thenReturn(admin);

        // Verify mocked save
        assertNotNull(adminRepository.save(admin), "Saved admin should not be null");

        String adminJson = """
                {
                    "email": "admin@example.com",
                    "password": "password123",
                    "firstname": "John",
                    "lastname": "Doe",
                    "institutionName": "ScholarHub"
                }
                """;

        MvcResult result = mockMvc.perform(post("/api/admins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(adminJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("admin@example.com"))
                .andReturn();

        // Verify response content
        String responseContent = result.getResponse().getContentAsString();
        Admin responseAdmin = objectMapper.readValue(responseContent, Admin.class);
        assertEquals(1L, responseAdmin.getId(), "Response ID should match");
        assertEquals("admin@example.com", responseAdmin.getEmail(), "Response email should match");
        assertEquals("John", responseAdmin.getFirstname(), "Response firstname should match");
    }

    @Test
    void testCreateAdmin_InvalidData() throws Exception {
        String invalidJson = """
                {
                    "email": "",
                    "password": "",
                    "firstname": "John",
                    "lastname": "Doe",
                    "institutionName": "ScholarHub"
                }
                """;

        MvcResult result = mockMvc.perform(post("/api/admins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andReturn();

        // Verify error response
        String responseContent = result.getResponse().getContentAsString();
        assertTrue(responseContent.contains("email") || responseContent.contains("password"),
                "Error response should mention email or password validation");
    }

    @Test
    void testGetAllAdmins() throws Exception {
        when(adminRepository.findAll()).thenReturn(Arrays.asList(admin));

        // Verify mocked return
        assertFalse(adminRepository.findAll().isEmpty(), "Admins list should not be empty");
        assertEquals(1, adminRepository.findAll().size(), "Admins list should have 1 item");

        MvcResult result = mockMvc.perform(get("/api/admins")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].email").value("admin@example.com"))
                .andReturn();

        // Verify response content
        String responseContent = result.getResponse().getContentAsString();
        Admin[] admins = objectMapper.readValue(responseContent, Admin[].class);
        assertEquals(1, admins.length, "Response should contain 1 admin");
        assertEquals(1L, admins[0].getId(), "Admin ID should match");
    }

    @Test
    void testGetAdminById_Success() throws Exception {
        when(adminRepository.findById(1L)).thenReturn(Optional.of(admin));

        // Verify mocked return
        assertTrue(adminRepository.findById(1L).isPresent(), "Admin should be found");
        assertEquals(1L, adminRepository.findById(1L).get().getId(), "Admin ID should match");

        MvcResult result = mockMvc.perform(get("/api/admins/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("admin@example.com"))
                .andReturn();

        // Verify response content
        String responseContent = result.getResponse().getContentAsString();
        Admin responseAdmin = objectMapper.readValue(responseContent, Admin.class);
        assertEquals(1L, responseAdmin.getId(), "Response ID should match");
        assertEquals("admin@example.com", responseAdmin.getEmail(), "Response email should match");
    }

    @Test
    void testGetAdminById_NotFound() throws Exception {
        when(adminRepository.findById(1L)).thenReturn(Optional.empty());

        // Verify mocked return
        assertTrue(adminRepository.findById(1L).isEmpty(), "Admin should not be found");

        mockMvc.perform(get("/api/admins/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}