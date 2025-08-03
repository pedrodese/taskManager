package com.ipaas.taskmanager.service;

import com.ipaas.taskmanager.domain.entity.Subtask;
import com.ipaas.taskmanager.domain.entity.Task;
import com.ipaas.taskmanager.domain.entity.User;
import com.ipaas.taskmanager.domain.enums.TaskStatus;
import com.ipaas.taskmanager.dto.request.CreateSubtaskDTO;
import com.ipaas.taskmanager.dto.request.UpdateSubtaskStatusDTO;
import com.ipaas.taskmanager.dto.response.SubtaskResponseDTO;
import com.ipaas.taskmanager.exception.subtask.SubtaskNotFoundException;
import com.ipaas.taskmanager.exception.task.TaskNotFoundException;
import com.ipaas.taskmanager.mapper.SubtaskMapper;
import com.ipaas.taskmanager.repository.SubtaskRepository;
import com.ipaas.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubtaskServiceTest {

    @Mock
    private SubtaskRepository subtaskRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private SubtaskMapper subtaskMapper;

    @InjectMocks
    private SubtaskService subtaskService;

    private UUID taskId;
    private UUID subtaskId;
    private UUID userId;
    private Task task;
    private User user;
    private Subtask subtask;
    private CreateSubtaskDTO createSubtaskDTO;
    private UpdateSubtaskStatusDTO updateSubtaskStatusDTO;
    private SubtaskResponseDTO subtaskResponseDTO;

    @BeforeEach
    void setUp() {
        taskId = UUID.randomUUID();
        subtaskId = UUID.randomUUID();
        userId = UUID.randomUUID();

        user = User.builder()
                .id(userId)
                .name("Test User")
                .email("test@email.com")
                .active(true)
                .build();

        task = Task.builder()
                .id(taskId)
                .title("Test Task")
                .description("Test Description")
                .status(TaskStatus.PENDING)
                .user(user)
                .build();

        subtask = Subtask.builder()
                .id(subtaskId)
                .title("Test Subtask")
                .description("Test Subtask Description")
                .status(TaskStatus.PENDING)
                .task(task)
                .createdAt(LocalDateTime.now())
                .build();

        createSubtaskDTO = CreateSubtaskDTO.builder()
                .title("New Subtask")
                .description("New Subtask Description")
                .build();

        updateSubtaskStatusDTO = UpdateSubtaskStatusDTO.builder()
                .status(TaskStatus.IN_PROGRESS)
                .build();

        subtaskResponseDTO = SubtaskResponseDTO.builder()
                .id(subtaskId)
                .title("Test Subtask")
                .description("Test Subtask Description")
                .status(TaskStatus.PENDING)
                .taskId(taskId)
                .build();
    }

    @Test
    void createSubtask_ShouldCreateSubtaskSuccessfully() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(subtaskMapper.toEntity(createSubtaskDTO)).thenReturn(subtask);
        when(subtaskRepository.save(any(Subtask.class))).thenReturn(subtask);
        when(subtaskMapper.toResponseDTO(subtask)).thenReturn(subtaskResponseDTO);

        SubtaskResponseDTO result = subtaskService.createSubtask(taskId, createSubtaskDTO);

        assertNotNull(result);
        assertEquals(subtaskId, result.getId());
        assertEquals("Test Subtask", result.getTitle());
        verify(taskRepository).findById(taskId);
        verify(subtaskMapper).toEntity(createSubtaskDTO);
        verify(subtaskRepository).save(any(Subtask.class));
        verify(subtaskMapper).toResponseDTO(subtask);
    }

    @Test
    void createSubtask_ShouldThrowException_WhenTaskNotFound() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> {
            subtaskService.createSubtask(taskId, createSubtaskDTO);
        });

        verify(taskRepository).findById(taskId);
        verify(subtaskRepository, never()).save(any());
    }

    @Test
    void getSubtasksByTaskId_ShouldReturnSubtasksList() {
        List<Subtask> subtasks = List.of(subtask);
        List<SubtaskResponseDTO> expectedResponse = List.of(subtaskResponseDTO);

        when(taskRepository.existsById(taskId)).thenReturn(true);
        when(subtaskRepository.findByTaskId(taskId)).thenReturn(subtasks);
        when(subtaskMapper.toResponseDTOList(subtasks)).thenReturn(expectedResponse);

        List<SubtaskResponseDTO> result = subtaskService.getSubtasksByTaskId(taskId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(subtaskId, result.get(0).getId());
        verify(taskRepository).existsById(taskId);
        verify(subtaskRepository).findByTaskId(taskId);
        verify(subtaskMapper).toResponseDTOList(subtasks);
    }

    @Test
    void getSubtasksByTaskId_ShouldThrowException_WhenTaskNotFound() {
        when(taskRepository.existsById(taskId)).thenReturn(false);

        assertThrows(TaskNotFoundException.class, () -> {
            subtaskService.getSubtasksByTaskId(taskId);
        });

        verify(taskRepository).existsById(taskId);
        verify(subtaskRepository, never()).findByTaskId(any());
    }

    @Test
    void updateSubtaskStatus_ShouldUpdateStatusSuccessfully() {
        Subtask updatedSubtask = Subtask.builder()
                .id(subtaskId)
                .title("Test Subtask")
                .description("Test Subtask Description")
                .status(TaskStatus.IN_PROGRESS)
                .task(task)
                .createdAt(LocalDateTime.now())
                .build();

        SubtaskResponseDTO updatedResponseDTO = SubtaskResponseDTO.builder()
                .id(subtaskId)
                .title("Test Subtask")
                .description("Test Subtask Description")
                .status(TaskStatus.IN_PROGRESS)
                .taskId(taskId)
                .build();

        when(subtaskRepository.findById(subtaskId)).thenReturn(Optional.of(subtask));
        when(subtaskRepository.save(any(Subtask.class))).thenReturn(updatedSubtask);
        when(subtaskMapper.toResponseDTO(updatedSubtask)).thenReturn(updatedResponseDTO);

        SubtaskResponseDTO result = subtaskService.updateSubtaskStatus(subtaskId, updateSubtaskStatusDTO);

        assertNotNull(result);
        assertEquals(TaskStatus.IN_PROGRESS, result.getStatus());
        verify(subtaskRepository).findById(subtaskId);
        verify(subtaskRepository).save(any(Subtask.class));
        verify(subtaskMapper).toResponseDTO(updatedSubtask);
    }

    @Test
    void updateSubtaskStatus_ShouldThrowException_WhenSubtaskNotFound() {
        when(subtaskRepository.findById(subtaskId)).thenReturn(Optional.empty());

        assertThrows(SubtaskNotFoundException.class, () -> {
            subtaskService.updateSubtaskStatus(subtaskId, updateSubtaskStatusDTO);
        });

        verify(subtaskRepository).findById(subtaskId);
        verify(subtaskRepository, never()).save(any());
    }

    @Test
    void getSubtaskById_ShouldReturnSubtask() {
        when(subtaskRepository.findById(subtaskId)).thenReturn(Optional.of(subtask));
        when(subtaskMapper.toResponseDTO(subtask)).thenReturn(subtaskResponseDTO);

        SubtaskResponseDTO result = subtaskService.getSubtaskById(subtaskId);

        assertNotNull(result);
        assertEquals(subtaskId, result.getId());
        assertEquals("Test Subtask", result.getTitle());
        verify(subtaskRepository).findById(subtaskId);
        verify(subtaskMapper).toResponseDTO(subtask);
    }

    @Test
    void getSubtaskById_ShouldThrowException_WhenSubtaskNotFound() {
        when(subtaskRepository.findById(subtaskId)).thenReturn(Optional.empty());

        assertThrows(SubtaskNotFoundException.class, () -> {
            subtaskService.getSubtaskById(subtaskId);
        });

        verify(subtaskRepository).findById(subtaskId);
        verify(subtaskMapper, never()).toResponseDTO(any());
    }
} 