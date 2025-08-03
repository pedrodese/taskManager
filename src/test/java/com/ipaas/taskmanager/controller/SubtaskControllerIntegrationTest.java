package com.ipaas.taskmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ipaas.taskmanager.domain.entity.Subtask;
import com.ipaas.taskmanager.domain.entity.Task;
import com.ipaas.taskmanager.domain.entity.User;
import com.ipaas.taskmanager.domain.enums.TaskStatus;
import com.ipaas.taskmanager.dto.request.CreateSubtaskDTO;
import com.ipaas.taskmanager.dto.request.UpdateSubtaskStatusDTO;
import com.ipaas.taskmanager.repository.SubtaskRepository;
import com.ipaas.taskmanager.repository.TaskRepository;
import com.ipaas.taskmanager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SubtaskControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private SubtaskRepository subtaskRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private User testUser;
    private Task testTask;
    private Subtask testSubtask;
    private UUID taskId;
    private UUID subtaskId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        testUser = User.builder()
                .name("Test User")
                .email("test@email.com")
                .active(true)
                .build();
        testUser = userRepository.save(testUser);

        testTask = Task.builder()
                .title("Test Task")
                .description("Test Task Description")
                .status(TaskStatus.PENDING)
                .user(testUser)
                .build();
        testTask = taskRepository.save(testTask);
        taskId = testTask.getId();

        testSubtask = Subtask.builder()
                .title("Test Subtask")
                .description("Test Subtask Description")
                .status(TaskStatus.PENDING)
                .task(testTask)
                .createdAt(LocalDateTime.now())
                .build();
        testSubtask = subtaskRepository.save(testSubtask);
        subtaskId = testSubtask.getId();
    }

    @Test
    void createSubtask_ShouldCreateSubtaskSuccessfully() throws Exception {
        CreateSubtaskDTO createSubtaskDTO = CreateSubtaskDTO.builder()
                .title("New Subtask")
                .description("New Subtask Description")
                .build();

        mockMvc.perform(post("/api/v1/tasks/{taskId}/subtasks", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createSubtaskDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("New Subtask"))
                .andExpect(jsonPath("$.description").value("New Subtask Description"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.taskId").value(taskId.toString()));
    }

    @Test
    void createSubtask_ShouldReturnBadRequest_WhenTitleIsEmpty() throws Exception {
        CreateSubtaskDTO createSubtaskDTO = CreateSubtaskDTO.builder()
                .title("")
                .description("Description")
                .build();

        mockMvc.perform(post("/api/v1/tasks/{taskId}/subtasks", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createSubtaskDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createSubtask_ShouldReturnNotFound_WhenTaskDoesNotExist() throws Exception {
        UUID nonExistentTaskId = UUID.randomUUID();
        CreateSubtaskDTO createSubtaskDTO = CreateSubtaskDTO.builder()
                .title("New Subtask")
                .description("Description")
                .build();

        mockMvc.perform(post("/api/v1/tasks/{taskId}/subtasks", nonExistentTaskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createSubtaskDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getSubtasksByTaskId_ShouldReturnSubtasksList() throws Exception {
        mockMvc.perform(get("/api/v1/tasks/{taskId}/subtasks", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(subtaskId.toString()))
                .andExpect(jsonPath("$[0].title").value("Test Subtask"))
                .andExpect(jsonPath("$[0].description").value("Test Subtask Description"))
                .andExpect(jsonPath("$[0].status").value("PENDING"))
                .andExpect(jsonPath("$[0].taskId").value(taskId.toString()));
    }

    @Test
    void getSubtasksByTaskId_ShouldReturnNotFound_WhenTaskDoesNotExist() throws Exception {
        UUID nonExistentTaskId = UUID.randomUUID();

        mockMvc.perform(get("/api/v1/tasks/{taskId}/subtasks", nonExistentTaskId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getSubtaskById_ShouldReturnSubtask() throws Exception {
        mockMvc.perform(get("/api/v1/subtasks/{subtaskId}", subtaskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(subtaskId.toString()))
                .andExpect(jsonPath("$.title").value("Test Subtask"))
                .andExpect(jsonPath("$.description").value("Test Subtask Description"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.taskId").value(taskId.toString()));
    }

    @Test
    void getSubtaskById_ShouldReturnNotFound_WhenSubtaskDoesNotExist() throws Exception {
        UUID nonExistentSubtaskId = UUID.randomUUID();

        mockMvc.perform(get("/api/v1/subtasks/{subtaskId}", nonExistentSubtaskId))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateSubtaskStatus_ShouldUpdateStatusSuccessfully() throws Exception {
        UpdateSubtaskStatusDTO updateSubtaskStatusDTO = UpdateSubtaskStatusDTO.builder()
                .status(TaskStatus.IN_PROGRESS)
                .build();

        mockMvc.perform(patch("/api/v1/subtasks/{subtaskId}/status", subtaskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateSubtaskStatusDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void updateSubtaskStatus_ShouldUpdateToCompleted() throws Exception {
        UpdateSubtaskStatusDTO updateSubtaskStatusDTO = UpdateSubtaskStatusDTO.builder()
                .status(TaskStatus.COMPLETED)
                .build();

        mockMvc.perform(patch("/api/v1/subtasks/{subtaskId}/status", subtaskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateSubtaskStatusDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.completedAt").exists());
    }

    @Test
    void updateSubtaskStatus_ShouldReturnNotFound_WhenSubtaskDoesNotExist() throws Exception {
        UUID nonExistentSubtaskId = UUID.randomUUID();
        UpdateSubtaskStatusDTO updateSubtaskStatusDTO = UpdateSubtaskStatusDTO.builder()
                .status(TaskStatus.IN_PROGRESS)
                .build();

        mockMvc.perform(patch("/api/v1/subtasks/{subtaskId}/status", nonExistentSubtaskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateSubtaskStatusDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateSubtaskStatus_ShouldReturnBadRequest_WhenStatusIsNull() throws Exception {
        UpdateSubtaskStatusDTO updateSubtaskStatusDTO = UpdateSubtaskStatusDTO.builder()
                .status(null)
                .build();

        mockMvc.perform(patch("/api/v1/subtasks/{subtaskId}/status", subtaskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateSubtaskStatusDTO)))
                .andExpect(status().isBadRequest());
    }
} 