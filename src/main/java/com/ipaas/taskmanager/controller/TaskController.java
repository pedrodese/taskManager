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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "Gerenciamento de tarefas")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @Operation(summary = "Criar tarefa", description = "Cria uma nova tarefa para um usuário")
    public ResponseEntity<TaskResponseDTO> createTask(@Valid @RequestBody CreateTaskDTO createTaskDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(createTaskDTO));
    }

    @GetMapping("/{taskId}")
    @Operation(summary = "Buscar tarefa", description = "Retorna uma tarefa por ID")
    public ResponseEntity<TaskResponseDTO> getTaskById(@PathVariable UUID taskId) {
        return ResponseEntity.ok(taskService.getTaskById(taskId));
    }

    @GetMapping
    @Operation(
        summary = "Listar tarefas", 
        description = "Lista tarefas com filtros opcionais (status, userId, title) e paginação"
    )
    public ResponseEntity<PageResponseDTO<TaskResponseDTO>> getTasks(
            @Parameter(description = "Status da tarefa") @RequestParam(required = false) TaskStatus status,
            @Parameter(description = "ID do usuário") @RequestParam(required = false) UUID userId,
            @Parameter(description = "Título da tarefa (busca parcial)") @RequestParam(required = false) String title,
            @Parameter(description = "Número da página (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(taskService.getTasksWithFilters(userId, status, title, pageable));
    }

    @PatchMapping("/{taskId}/status")
    @Operation(summary = "Atualizar status", description = "Atualiza o status de uma tarefa")
    public ResponseEntity<TaskResponseDTO> updateTaskStatus(
            @Parameter(description = "ID da tarefa") @PathVariable UUID taskId,
            @Valid @RequestBody UpdateTaskStatusDTO updateTaskStatusDTO) {
        return ResponseEntity.ok(taskService.updateTaskStatus(taskId, updateTaskStatusDTO));
    }
} 