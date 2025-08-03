package com.ipaas.taskmanager.dto.response;

import com.ipaas.taskmanager.domain.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponseDTO {

    private UUID id;
    private String title;
    private String description;
    private TaskStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;
    private UUID userId;
    private String userName;
    private String userEmail;
    private List<SubtaskResponseDTO> subtasks;
    private Integer totalSubtasks;
    private Integer completedSubtasks;
} 