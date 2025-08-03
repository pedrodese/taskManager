package com.ipaas.taskmanager.controller;

import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ipaas.taskmanager.domain.enums.TaskStatus;
import com.ipaas.taskmanager.dto.request.CreateTaskDTO;
import com.ipaas.taskmanager.dto.request.UpdateTaskStatusDTO;
import com.ipaas.taskmanager.dto.response.PageResponseDTO;
import com.ipaas.taskmanager.dto.response.TaskResponseDTO;
import com.ipaas.taskmanager.service.TaskService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponseDTO> createTask(@Valid @RequestBody CreateTaskDTO createTaskDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(createTaskDTO));
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponseDTO> getTaskById(@PathVariable UUID taskId) {
        return ResponseEntity.ok(taskService.getTaskById(taskId));
    }

    @GetMapping
    public ResponseEntity<PageResponseDTO<TaskResponseDTO>> getTasks(
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(taskService.getTasksWithFilters(userId, status, title, pageable));
    }

    @PatchMapping("/{taskId}/status")
    public ResponseEntity<TaskResponseDTO> updateTaskStatus(
            @PathVariable UUID taskId,
            @Valid @RequestBody UpdateTaskStatusDTO updateTaskStatusDTO) {
        return ResponseEntity.ok(taskService.updateTaskStatus(taskId, updateTaskStatusDTO));
    }
} 