package com.example.demo.Program;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProgramController.class)
public class ProgramControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProgramRepository programRepository;

    private Program program;

  @BeforeEach
void setUp() {
    program = new Program();
    program.setId(1L);
    // Assuming Program has setProgramName(String)
    try {
        program.getClass().getMethod("setProgramName", String.class).invoke(program, "Program 1");
    } catch (NoSuchMethodException e) {
        try {
            program.getClass().getMethod("setName", String.class).invoke(program, "Program 1");
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
            throw new RuntimeException("Failed to set program name", ex);
        }
    } catch (IllegalAccessException | InvocationTargetException e) {
        throw new RuntimeException("Failed to invoke method on program", e);
    }
}


    @Test
    void testCreateProgram_Success() throws Exception {
        when(programRepository.save(any(Program.class))).thenReturn(program);

        String programJson = objectMapper.writeValueAsString(program);

        mockMvc.perform(post("/api/programs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(programJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.programName").value("Program 1"));

        assertNotNull(programRepository.save(program));
    }

    @Test
    void testUpdateProgram_Success() throws Exception {
        when(programRepository.existsById(1L)).thenReturn(true);
        when(programRepository.save(any(Program.class))).thenReturn(program);

        String programJson = objectMapper.writeValueAsString(program);

        mockMvc.perform(put("/api/programs/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(programJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.programName").value("Program 1"));

        assertEquals("Program 1", programRepository.save(program).getClass().getMethod("getProgramName").invoke(program));
    }

    @Test
    void testUpdateProgram_NotFound() throws Exception {
        when(programRepository.existsById(1L)).thenReturn(false);

        String programJson = objectMapper.writeValueAsString(program);

        mockMvc.perform(put("/api/programs/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(programJson))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllPrograms() throws Exception {
        when(programRepository.findAll()).thenReturn(Arrays.asList(program));

        mockMvc.perform(get("/api/programs")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].programName").value("Program 1"));

        assertFalse(programRepository.findAll().isEmpty());
    }

    @Test
    void testGetProgramById_Success() throws Exception {
        when(programRepository.findById(1L)).thenReturn(Optional.of(program));

        mockMvc.perform(get("/api/programs/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.programName").value("Program 1"));

        assertTrue(programRepository.findById(1L).isPresent());
    }

    @Test
    void testGetProgramById_NotFound() throws Exception {
        when(programRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/programs/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteProgram_Success() throws Exception {
        when(programRepository.existsById(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/programs/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        assertTrue(programRepository.existsById(1L));
    }

    @Test
    void testDeleteProgram_NotFound() throws Exception {
        when(programRepository.existsById(1L)).thenReturn(false);

        mockMvc.perform(delete("/api/programs/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}