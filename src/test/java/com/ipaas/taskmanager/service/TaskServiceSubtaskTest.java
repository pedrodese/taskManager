package com.ipaas.taskmanager.service;

import com.ipaas.taskmanager.domain.entity.Subtask;
import com.ipaas.taskmanager.domain.entity.Task;
import com.ipaas.taskmanager.domain.entity.User;
import com.ipaas.taskmanager.domain.enums.TaskStatus;
import com.ipaas.taskmanager.dto.request.UpdateTaskStatusDTO;
import com.ipaas.taskmanager.exception.task.TaskCannotBeCompletedException;
import com.ipaas.taskmanager.repository.SubtaskRepository;
import com.ipaas.taskmanager.repository.TaskRepository;
import com.ipaas.taskmanager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TaskServiceSubtaskTest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private SubtaskRepository subtaskRepository;

    private User testUser;
    private Task testTask;
    private Subtask subtask1;
    private Subtask subtask2;

    @BeforeEach
    void setUp() {
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
    }

    @Test
    void updateTaskStatus_ShouldCompleteTask_WhenAllSubtasksAreCompleted() {
        subtask1 = Subtask.builder()
                .title("Subtask 1")
                .description("Description 1")
                .status(TaskStatus.PENDING)
                .task(testTask)
                .createdAt(LocalDateTime.now())
                .build();
        subtask1 = subtaskRepository.save(subtask1);

        subtask2 = Subtask.builder()
                .title("Subtask 2")
                .description("Description 2")
                .status(TaskStatus.PENDING)
                .task(testTask)
                .createdAt(LocalDateTime.now())
                .build();
        subtask2 = subtaskRepository.save(subtask2);

        subtask1.setStatus(TaskStatus.COMPLETED);
        subtask1.setCompletedAt(LocalDateTime.now());
        subtaskRepository.save(subtask1);

        subtask2.setStatus(TaskStatus.COMPLETED);
        subtask2.setCompletedAt(LocalDateTime.now());
        subtaskRepository.save(subtask2);

        UpdateTaskStatusDTO updateTaskStatusDTO = UpdateTaskStatusDTO.builder()
                .status(TaskStatus.COMPLETED)
                .build();

        var result = taskService.updateTaskStatus(testTask.getId(), updateTaskStatusDTO);

        assertNotNull(result);
        assertEquals(TaskStatus.COMPLETED, result.getStatus());
        assertNotNull(result.getCompletedAt());
    }

    @Test
    void updateTaskStatus_ShouldThrowException_WhenTaskHasIncompleteSubtasks() {
        subtask1 = Subtask.builder()
                .title("Subtask 1")
                .description("Description 1")
                .status(TaskStatus.PENDING)
                .task(testTask)
                .createdAt(LocalDateTime.now())
                .build();
        subtask1 = subtaskRepository.save(subtask1);

        subtask2 = Subtask.builder()
                .title("Subtask 2")
                .description("Description 2")
                .status(TaskStatus.IN_PROGRESS)
                .task(testTask)
                .createdAt(LocalDateTime.now())
                .build();
        subtask2 = subtaskRepository.save(subtask2);

        UpdateTaskStatusDTO updateTaskStatusDTO = UpdateTaskStatusDTO.builder()
                .status(TaskStatus.COMPLETED)
                .build();

        assertThrows(TaskCannotBeCompletedException.class, () -> {
            taskService.updateTaskStatus(testTask.getId(), updateTaskStatusDTO);
        });
    }

    @Test
    void updateTaskStatus_ShouldThrowException_WhenTaskHasSomeIncompleteSubtasks() {
        subtask1 = Subtask.builder()
                .title("Subtask 1")
                .description("Description 1")
                .status(TaskStatus.COMPLETED)
                .task(testTask)
                .createdAt(LocalDateTime.now())
                .completedAt(LocalDateTime.now())
                .build();
        subtask1 = subtaskRepository.save(subtask1);

        subtask2 = Subtask.builder()
                .title("Subtask 2")
                .description("Description 2")
                .status(TaskStatus.PENDING)
                .task(testTask)
                .createdAt(LocalDateTime.now())
                .build();
        subtask2 = subtaskRepository.save(subtask2);

        UpdateTaskStatusDTO updateTaskStatusDTO = UpdateTaskStatusDTO.builder()
                .status(TaskStatus.COMPLETED)
                .build();

        assertThrows(TaskCannotBeCompletedException.class, () -> {
            taskService.updateTaskStatus(testTask.getId(), updateTaskStatusDTO);
        });
    }

    @Test
    void updateTaskStatus_ShouldCompleteTask_WhenTaskHasNoSubtasks() {
        UpdateTaskStatusDTO updateTaskStatusDTO = UpdateTaskStatusDTO.builder()
                .status(TaskStatus.COMPLETED)
                .build();

        var result = taskService.updateTaskStatus(testTask.getId(), updateTaskStatusDTO);

        assertNotNull(result);
        assertEquals(TaskStatus.COMPLETED, result.getStatus());
        assertNotNull(result.getCompletedAt());
    }

    @Test
    void updateTaskStatus_ShouldCompleteTask_WhenAllSubtasksAreInProgress() {
        subtask1 = Subtask.builder()
                .title("Subtask 1")
                .description("Description 1")
                .status(TaskStatus.IN_PROGRESS)
                .task(testTask)
                .createdAt(LocalDateTime.now())
                .build();
        subtask1 = subtaskRepository.save(subtask1);

        subtask2 = Subtask.builder()
                .title("Subtask 2")
                .description("Description 2")
                .status(TaskStatus.IN_PROGRESS)
                .task(testTask)
                .createdAt(LocalDateTime.now())
                .build();
        subtask2 = subtaskRepository.save(subtask2);

        UpdateTaskStatusDTO updateTaskStatusDTO = UpdateTaskStatusDTO.builder()
                .status(TaskStatus.COMPLETED)
                .build();

        assertThrows(TaskCannotBeCompletedException.class, () -> {
            taskService.updateTaskStatus(testTask.getId(), updateTaskStatusDTO);
        });
    }
} 