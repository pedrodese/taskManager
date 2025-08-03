package com.ipaas.taskmanager.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ipaas.taskmanager.domain.entity.Subtask;
import com.ipaas.taskmanager.domain.entity.Task;
import com.ipaas.taskmanager.domain.entity.User;
import com.ipaas.taskmanager.domain.enums.TaskStatus;
import com.ipaas.taskmanager.dto.request.CreateTaskDTO;
import com.ipaas.taskmanager.dto.request.UpdateTaskStatusDTO;
import com.ipaas.taskmanager.dto.response.PageResponseDTO;
import com.ipaas.taskmanager.dto.response.TaskResponseDTO;
import com.ipaas.taskmanager.exception.task.TaskCannotBeCompletedException;
import com.ipaas.taskmanager.exception.task.TaskNotFoundException;
import com.ipaas.taskmanager.exception.task.InvalidTaskStatusTransitionException;
import com.ipaas.taskmanager.exception.user.UserInactiveException;
import com.ipaas.taskmanager.exception.user.UserNotFoundException;
import com.ipaas.taskmanager.mapper.TaskMapper;
import com.ipaas.taskmanager.repository.SubtaskRepository;
import com.ipaas.taskmanager.repository.TaskRepository;
import com.ipaas.taskmanager.repository.UserRepository;

import lombok.RequiredArgsConstructor;  

@Service
@RequiredArgsConstructor
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final SubtaskRepository subtaskRepository;
    private final TaskMapper taskMapper;

    public TaskResponseDTO createTask(CreateTaskDTO createTaskDTO) {
        User user = userRepository.findById(createTaskDTO.getUserId())
            .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + createTaskDTO.getUserId()));
        
        if (!user.getActive()) {
            throw new UserInactiveException("Cannot create task for inactive user: " + createTaskDTO.getUserId());
        }
        
        Task task = taskMapper.toEntity(createTaskDTO);
        task.setUser(user);
        taskRepository.save(task);
        
        return taskMapper.toDTO(task);
    }

    public TaskResponseDTO getTaskById(UUID taskId) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new TaskNotFoundException("Task not found with ID: " + taskId));
        
        return taskMapper.toDTO(task);
    }



    public PageResponseDTO<TaskResponseDTO> getTasksWithFilters(UUID userId, TaskStatus status, String title, Pageable pageable) {
        Page<Task> tasks = taskRepository.findTasksWithFilters(userId, status, title, pageable);
        
        List<TaskResponseDTO> filteredTasks = tasks.getContent().stream()
            .map(taskMapper::toDTO)
            .filter(task -> title == null || title.isEmpty() || 
                task.getTitle().toLowerCase().contains(title.toLowerCase()))
            .toList();
        
        Page<TaskResponseDTO> tasksPage = new PageImpl<>(
            filteredTasks, 
            pageable, 
            tasks.getTotalElements()
        );
        
        return PageResponseDTO.<TaskResponseDTO>builder()
                .content(tasksPage.getContent())
                .totalElements(tasksPage.getTotalElements())
                .totalPages(tasksPage.getTotalPages())
                .pageNumber(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .hasNext(tasksPage.hasNext())
                .hasPrevious(tasksPage.hasPrevious())
                .build();
    }

    public TaskResponseDTO updateTaskStatus(UUID taskId, UpdateTaskStatusDTO updateTaskStatusDTO) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new TaskNotFoundException("Task not found with ID: " + taskId));
        
        TaskStatus newStatus = updateTaskStatusDTO.getStatus();
        TaskStatus currentStatus = task.getStatus();
        
        validateStatusTransition(currentStatus, newStatus);
        
        if (newStatus == TaskStatus.COMPLETED) {
            validateTaskCanBeCompleted(task);
        }
        
        task.setStatus(newStatus);
        
        if (newStatus == TaskStatus.COMPLETED) {
            task.setCompletedAt(LocalDateTime.now());
        } else if (currentStatus == TaskStatus.COMPLETED && newStatus != TaskStatus.COMPLETED) {
            task.setCompletedAt(null);
        }
        
        Task updatedTask = taskRepository.save(task);
        
        return taskMapper.toDTO(updatedTask);
    }

    private void validateStatusTransition(TaskStatus currentStatus, TaskStatus newStatus) {
        if (currentStatus == newStatus) {
            throw new InvalidTaskStatusTransitionException("Cannot update status to the same value: " + currentStatus);
        }
        if (currentStatus == TaskStatus.COMPLETED && newStatus != TaskStatus.COMPLETED) {
            throw new InvalidTaskStatusTransitionException("Cannot revert a completed task");
        }
    }

    private void validateTaskCanBeCompleted(Task task) {
        List<Subtask> subtasks = subtaskRepository.findByTaskId(task.getId());
        if (!subtasks.isEmpty()) {
            boolean hasIncompleteSubtasks = subtasks.stream()
                .anyMatch(subtask -> subtask.getStatus() != TaskStatus.COMPLETED);
            
            if (hasIncompleteSubtasks) {
                throw new TaskCannotBeCompletedException(
                    "Cannot complete task with ID " + task.getId() + " because it has incomplete subtasks"
                );
            }
        }
    }


} 