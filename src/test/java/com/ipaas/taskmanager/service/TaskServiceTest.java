package com.ipaas.taskmanager.service;

import com.ipaas.taskmanager.domain.entity.Task;
import com.ipaas.taskmanager.domain.entity.User;
import com.ipaas.taskmanager.domain.enums.TaskStatus;
import com.ipaas.taskmanager.dto.request.CreateTaskDTO;
import com.ipaas.taskmanager.dto.request.UpdateTaskStatusDTO;
import com.ipaas.taskmanager.dto.response.PageResponseDTO;
import com.ipaas.taskmanager.dto.response.TaskResponseDTO;
import com.ipaas.taskmanager.exception.task.TaskNotFoundException;
import com.ipaas.taskmanager.exception.user.UserInactiveException;
import com.ipaas.taskmanager.exception.user.UserNotFoundException;
import com.ipaas.taskmanager.mapper.TaskMapper;
import com.ipaas.taskmanager.repository.TaskRepository;
import com.ipaas.taskmanager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskService taskService;

    private UUID taskId;
    private UUID userId;
    private Task task;
    private User user;
    private CreateTaskDTO createTaskDTO;
    private UpdateTaskStatusDTO updateTaskStatusDTO;
    private TaskResponseDTO taskResponseDTO;

    @BeforeEach
    void setUp() {
        taskId = UUID.randomUUID();
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
                .createdAt(LocalDateTime.now())
                .build();

        createTaskDTO = CreateTaskDTO.builder()
                .title("New Task")
                .description("New Task Description")
                .userId(userId)
                .build();

        updateTaskStatusDTO = UpdateTaskStatusDTO.builder()
                .status(TaskStatus.IN_PROGRESS)
                .build();

        taskResponseDTO = TaskResponseDTO.builder()
                .id(taskId)
                .title("Test Task")
                .description("Test Description")
                .status(TaskStatus.PENDING)
                .userId(userId)
                .build();
    }

    @Test
    void createTask_ShouldCreateTaskSuccessfully() {
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(taskMapper.toEntity(createTaskDTO)).thenReturn(task);
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskMapper.toDTO(task)).thenReturn(taskResponseDTO);

        
        TaskResponseDTO result = taskService.createTask(createTaskDTO);

        
        assertNotNull(result);
        assertEquals(taskId, result.getId());
        assertEquals("Test Task", result.getTitle());
        verify(userRepository).findById(userId);
        verify(taskMapper).toEntity(createTaskDTO);
        verify(taskRepository).save(any(Task.class));
        verify(taskMapper).toDTO(task);
    }

    @Test
    void createTask_ShouldThrowException_WhenUserNotFound() {
        
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        
        assertThrows(UserNotFoundException.class, () -> {
            taskService.createTask(createTaskDTO);
        });

        verify(userRepository).findById(userId);
        verify(taskRepository, never()).save(any());
    }

    @Test
    void createTask_ShouldThrowException_WhenUserIsInactive() {
        
        user.setActive(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        
        assertThrows(UserInactiveException.class, () -> {
            taskService.createTask(createTaskDTO);
        });

        verify(userRepository).findById(userId);
        verify(taskRepository, never()).save(any());
    }

    @Test
    void getTaskById_ShouldReturnTask() {
        
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskMapper.toDTO(task)).thenReturn(taskResponseDTO);

        
        TaskResponseDTO result = taskService.getTaskById(taskId);

        
        assertNotNull(result);
        assertEquals(taskId, result.getId());
        assertEquals("Test Task", result.getTitle());
        verify(taskRepository).findById(taskId);
        verify(taskMapper).toDTO(task);
    }

    @Test
    void getTaskById_ShouldThrowException_WhenTaskNotFound() {
        
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        
        assertThrows(TaskNotFoundException.class, () -> {
            taskService.getTaskById(taskId);
        });

        verify(taskRepository).findById(taskId);
        verify(taskMapper, never()).toDTO(any());
    }

    @Test
    void getTasksWithFilters_ShouldReturnFilteredTasks() {

        Pageable pageable = PageRequest.of(0, 10);
        List<Task> tasks = List.of(task);
        Page<Task> taskPage = new PageImpl<>(tasks, pageable, 1);

        when(taskRepository.findTasksWithFilters(userId, TaskStatus.PENDING, "test", pageable))
                .thenReturn(taskPage);
        when(taskMapper.toDTO(task)).thenReturn(taskResponseDTO);

        
        PageResponseDTO<TaskResponseDTO> result = taskService.getTasksWithFilters(userId, TaskStatus.PENDING, "test", pageable);

        
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(taskRepository).findTasksWithFilters(userId, TaskStatus.PENDING, "test", pageable);
    }

    @Test
    void updateTaskStatus_ShouldUpdateStatusSuccessfully() {
        
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskMapper.toDTO(task)).thenReturn(taskResponseDTO);

        
        TaskResponseDTO result = taskService.updateTaskStatus(taskId, updateTaskStatusDTO);

        
        assertNotNull(result);
        verify(taskRepository).findById(taskId);
        verify(taskRepository).save(any(Task.class));
        verify(taskMapper).toDTO(task);
    }

    @Test
    void updateTaskStatus_ShouldThrowException_WhenTaskNotFound() {
        
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        
        assertThrows(TaskNotFoundException.class, () -> {
            taskService.updateTaskStatus(taskId, updateTaskStatusDTO);
        });

        verify(taskRepository).findById(taskId);
        verify(taskRepository, never()).save(any());
    }
} 