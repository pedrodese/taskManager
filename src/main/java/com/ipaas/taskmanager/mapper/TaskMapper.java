package com.ipaas.taskmanager.mapper;

import com.ipaas.taskmanager.domain.entity.Subtask;
import com.ipaas.taskmanager.domain.entity.Task;
import com.ipaas.taskmanager.dto.request.CreateTaskDTO;
import com.ipaas.taskmanager.dto.response.SubtaskResponseDTO;
import com.ipaas.taskmanager.dto.response.TaskResponseDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TaskMapper {

    public Task toEntity(CreateTaskDTO createTaskDTO) {
        return Task.builder()
                .title(createTaskDTO.getTitle())
                .description(createTaskDTO.getDescription())
                .build();
    }

    public TaskResponseDTO toDTO(Task task) {
        List<SubtaskResponseDTO> subtaskDTOs = null;
        Integer totalSubtasks = 0;
        Integer completedSubtasks = 0;

        if (task.getSubtasks() != null) {
            subtaskDTOs = task.getSubtasks().stream()
                    .map(this::toSubtaskDTO)
                    .collect(Collectors.toList());
            
            totalSubtasks = task.getSubtasks().size();
            completedSubtasks = (int) task.getSubtasks().stream()
                    .filter(subtask -> subtask.getStatus().name().equals("COMPLETED"))
                    .count();
        }

        return TaskResponseDTO.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .completedAt(task.getCompletedAt())
                .userId(task.getUser().getId())
                .userName(task.getUser().getName())
                .userEmail(task.getUser().getEmail())
                .subtasks(subtaskDTOs)
                .totalSubtasks(totalSubtasks)
                .completedSubtasks(completedSubtasks)
                .build();
    }

    private SubtaskResponseDTO toSubtaskDTO(Subtask subtask) {
        return SubtaskResponseDTO.builder()
                .id(subtask.getId())
                .title(subtask.getTitle())
                .description(subtask.getDescription())
                .status(subtask.getStatus())
                .createdAt(subtask.getCreatedAt())
                .updatedAt(subtask.getUpdatedAt())
                .completedAt(subtask.getCompletedAt())
                .taskId(subtask.getTask().getId())
                .build();
    }
} 