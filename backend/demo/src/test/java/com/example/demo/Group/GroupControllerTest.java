package com.example.demo.Group;

import com.example.demo.Level.Level;
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

@WebMvcTest(GroupController.class)
public class GroupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GroupRepository groupRepository;

    @MockBean
    private com.example.demo.Level.LevelRepository levelRepository;

    private Group group;
    private Level level;

    @BeforeEach
    void setUp() {
        level = new Level();
        level.setId(1L);
        level.setName("Level 1");

        group = new Group();
        group.setId(1L);
        group.setName("Group A");
        group.setLevel(level);
    }

    @Test
    void testCreateGroup_Success() throws Exception {
        when(levelRepository.existsById(1L)).thenReturn(true);
        when(groupRepository.save(any(Group.class))).thenReturn(group);

        // Verify mocked save
        assertNotNull(groupRepository.save(group), "Saved group should not be null");
        assertTrue(levelRepository.existsById(1L), "Level should exist");

        String groupJson = """
                {
                    "name": "Group A",
                    "level": {"id": 1}
                }
                """;

        MvcResult result = mockMvc.perform(post("/api/groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(groupJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Group A"))
                .andReturn();

        // Verify response content
        String responseContent = result.getResponse().getContentAsString();
        Group responseGroup = objectMapper.readValue(responseContent, Group.class);
        assertEquals(1L, responseGroup.getId(), "Response ID should match");
        assertEquals("Group A", responseGroup.getName(), "Response name should match");
        assertNotNull(responseGroup.getLevel(), "Level should not be null");
        assertEquals(1L, responseGroup.getLevel().getId(), "Level ID should match");
    }

    @Test
    void testCreateGroup_LevelNotFound() throws Exception {
        when(levelRepository.existsById(1L)).thenReturn(false);

        // Verify mocked return
        assertFalse(levelRepository.existsById(1L), "Level should not exist");

        String groupJson = """
                {
                    "name": "Group A",
                    "level": {"id": 1}
                }
                """;

        mockMvc.perform(post("/api/groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(groupJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllGroups() throws Exception {
        when(groupRepository.findAll()).thenReturn(Arrays.asList(group));

        // Verify mocked return
        assertFalse(groupRepository.findAll().isEmpty(), "Groups list should not be empty");
        assertEquals(1, groupRepository.findAll().size(), "Groups list should have 1 item");

        MvcResult result = mockMvc.perform(get("/api/groups")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Group A"))
                .andReturn();

        // Verify response content
        String responseContent = result.getResponse().getContentAsString();
        Group[] groups = objectMapper.readValue(responseContent, Group[].class);
        assertEquals(1, groups.length, "Response should contain 1 group");
        assertEquals(1L, groups[0].getId(), "Group ID should match");
    }

    @Test
    void testGetGroupById_Success() throws Exception {
        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));

        // Verify mocked return
        assertTrue(groupRepository.findById(1L).isPresent(), "Group should be found");
        assertEquals(1L, groupRepository.findById(1L).get().getId(), "Group ID should match");

        MvcResult result = mockMvc.perform(get("/api/groups/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Group A"))
                .andReturn();

        // Verify response content
        String responseContent = result.getResponse().getContentAsString();
        Group responseGroup = objectMapper.readValue(responseContent, Group.class);
        assertEquals(1L, responseGroup.getId(), "Response ID should match");
        assertEquals("Group A", responseGroup.getName(), "Response name should match");
    }

    @Test
    void testGetGroupById_NotFound() throws Exception {
        when(groupRepository.findById(1L)).thenReturn(Optional.empty());

        // Verify mocked return
        assertTrue(groupRepository.findById(1L).isEmpty(), "Group should not be found");

        mockMvc.perform(get("/api/groups/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}