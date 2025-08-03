package com.ipaas.taskmanager.service;

import com.ipaas.taskmanager.domain.entity.Subtask;
import com.ipaas.taskmanager.domain.entity.Task;
import com.ipaas.taskmanager.domain.enums.TaskStatus;
import com.ipaas.taskmanager.dto.request.CreateSubtaskDTO;
import com.ipaas.taskmanager.dto.request.UpdateSubtaskStatusDTO;
import com.ipaas.taskmanager.dto.response.SubtaskResponseDTO;
import com.ipaas.taskmanager.exception.task.TaskNotFoundException;
import com.ipaas.taskmanager.exception.subtask.SubtaskNotFoundException;
import com.ipaas.taskmanager.mapper.SubtaskMapper;
import com.ipaas.taskmanager.repository.SubtaskRepository;
import com.ipaas.taskmanager.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class SubtaskService {

    private final SubtaskRepository subtaskRepository;
    private final TaskRepository taskRepository;
    private final SubtaskMapper subtaskMapper;

    public SubtaskResponseDTO createSubtask(UUID taskId, CreateSubtaskDTO createSubtaskDTO) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + taskId));

        Subtask subtask = subtaskMapper.toEntity(createSubtaskDTO);
        subtask.setTask(task);
        
        Subtask savedSubtask = subtaskRepository.save(subtask);
        return subtaskMapper.toResponseDTO(savedSubtask);
    }

    public List<SubtaskResponseDTO> getSubtasksByTaskId(UUID taskId) {
        // Verificar se a tarefa existe
        if (!taskRepository.existsById(taskId)) {
            throw new TaskNotFoundException("Task not found with id: " + taskId);
        }

        List<Subtask> subtasks = subtaskRepository.findByTaskId(taskId);
        return subtaskMapper.toResponseDTOList(subtasks);
    }

    public SubtaskResponseDTO updateSubtaskStatus(UUID subtaskId, UpdateSubtaskStatusDTO updateSubtaskStatusDTO) {
        Subtask subtask = subtaskRepository.findById(subtaskId)
                .orElseThrow(() -> new SubtaskNotFoundException("Subtask not found with id: " + subtaskId));

        TaskStatus newStatus = updateSubtaskStatusDTO.getStatus();
        subtask.setStatus(newStatus);

        // Atualizar data de conclusão se o status for COMPLETED
        if (newStatus == TaskStatus.COMPLETED) {
            subtask.setCompletedAt(LocalDateTime.now());
        } else {
            subtask.setCompletedAt(null);
        }

        Subtask updatedSubtask = subtaskRepository.save(subtask);
        return subtaskMapper.toResponseDTO(updatedSubtask);
    }

    public SubtaskResponseDTO getSubtaskById(UUID subtaskId) {
        Subtask subtask = subtaskRepository.findById(subtaskId)
                .orElseThrow(() -> new SubtaskNotFoundException("Subtask not found with id: " + subtaskId));
        
        return subtaskMapper.toResponseDTO(subtask);
    }
} 