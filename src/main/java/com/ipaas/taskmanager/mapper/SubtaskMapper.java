package com.ipaas.taskmanager.mapper;

import com.ipaas.taskmanager.domain.entity.Subtask;
import com.ipaas.taskmanager.dto.request.CreateSubtaskDTO;
import com.ipaas.taskmanager.dto.response.SubtaskResponseDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SubtaskMapper {

    public Subtask toEntity(CreateSubtaskDTO createSubtaskDTO) {
        return Subtask.builder()
                .title(createSubtaskDTO.getTitle())
                .description(createSubtaskDTO.getDescription())
                .build();
    }

    public SubtaskResponseDTO toResponseDTO(Subtask subtask) {
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

    public List<SubtaskResponseDTO> toResponseDTOList(List<Subtask> subtasks) {
        return subtasks.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }
} 