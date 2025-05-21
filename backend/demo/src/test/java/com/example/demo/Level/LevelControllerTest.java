package com.example.demo.Level;

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

@WebMvcTest(LevelController.class)
public class LevelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LevelRepository levelRepository;

    private Level level;

    @BeforeEach
    void setUp() {
        level = new Level();
        level.setId(1L);
        level.setName("Level 1");
    }

    @Test
    void testCreateLevel_Success() throws Exception {
        when(levelRepository.save(any(Level.class))).thenReturn(level);

        // Verify mocked save
        assertNotNull(levelRepository.save(level), "Saved level should not be null");

        String levelJson = """
                {
                    "name": "Level 1"
                }
                """;

        MvcResult result = mockMvc.perform(post("/api/levels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(levelJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Level 1"))
                .andReturn();

        // Verify response content
        String responseContent = result.getResponse().getContentAsString();
        Level responseLevel = objectMapper.readValue(responseContent, Level.class);
        assertEquals(1L, responseLevel.getId(), "Response ID should match");
        assertEquals("Level 1", responseLevel.getName(), "Response name should match");
    }

    @Test
    void testCreateLevel_InvalidData() throws Exception {
        String invalidJson = """
                {
                    "name": ""
                }
                """;

        MvcResult result = mockMvc.perform(post("/api/levels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andReturn();

        // Verify error response
        String responseContent = result.getResponse().getContentAsString();
        assertTrue(responseContent.contains("name"), "Error response should mention name validation");
    }

    @Test
    void testGetAllLevels() throws Exception {
        when(levelRepository.findAll()).thenReturn(Arrays.asList(level));

        // Verify mocked return
        assertFalse(levelRepository.findAll().isEmpty(), "Levels list should not be empty");
        assertEquals(1, levelRepository.findAll().size(), "Levels list should have 1 item");

        MvcResult result = mockMvc.perform(get("/api/levels")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Level 1"))
                .andReturn();

        // Verify response content
        String responseContent = result.getResponse().getContentAsString();
        Level[] levels = objectMapper.readValue(responseContent, Level[].class);
        assertEquals(1, levels.length, "Response should contain 1 level");
        assertEquals(1L, levels[0].getId(), "Level ID should match");
    }

    @Test
    void testGetLevelById_Success() throws Exception {
        when(levelRepository.findById(1L)).thenReturn(Optional.of(level));

        // Verify mocked return
        assertTrue(levelRepository.findById(1L).isPresent(), "Level should be found");
        assertEquals(1L, levelRepository.findById(1L).get().getId(), "Level ID should match");

        MvcResult result = mockMvc.perform(get("/api/levels/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Level 1"))
                .andReturn();

        // Verify response content
        String responseContent = result.getResponse().getContentAsString();
        Level responseLevel = objectMapper.readValue(responseContent, Level.class);
        assertEquals(1L, responseLevel.getId(), "Response ID should match");
        assertEquals("Level 1", responseLevel.getName(), "Response name should match");
    }

    @Test
    void testGetLevelById_NotFound() throws Exception {
        when(levelRepository.findById(1L)).thenReturn(Optional.empty());

        // Verify mocked return
        assertTrue(levelRepository.findById(1L).isEmpty(), "Level should not be found");

        mockMvc.perform(get("/api/levels/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}