package com.ipaas.taskmanager.controller;

import com.ipaas.taskmanager.dto.request.CreateSubtaskDTO;
import com.ipaas.taskmanager.dto.request.UpdateSubtaskStatusDTO;
import com.ipaas.taskmanager.dto.response.SubtaskResponseDTO;
import com.ipaas.taskmanager.service.SubtaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Subtasks", description = "Gerenciamento de subtarefas")
public class SubtaskController {

    private final SubtaskService subtaskService;

    @PostMapping("/tasks/{taskId}/subtasks")
    @Operation(summary = "Criar subtarefa", description = "Cria uma nova subtarefa para uma tarefa específica")
    public ResponseEntity<SubtaskResponseDTO> createSubtask(
            @Parameter(description = "ID da tarefa") @PathVariable UUID taskId,
            @Valid @RequestBody CreateSubtaskDTO createSubtaskDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(subtaskService.createSubtask(taskId, createSubtaskDTO));
    }

    @GetMapping("/tasks/{taskId}/subtasks")
    @Operation(summary = "Listar subtarefas", description = "Lista todas as subtarefas de uma tarefa específica")
    public ResponseEntity<List<SubtaskResponseDTO>> getSubtasksByTaskId(
            @Parameter(description = "ID da tarefa") @PathVariable UUID taskId) {
        return ResponseEntity.ok(subtaskService.getSubtasksByTaskId(taskId));
    }

    @GetMapping("/subtasks/{subtaskId}")
    @Operation(summary = "Buscar subtarefa", description = "Retorna uma subtarefa específica por ID")
    public ResponseEntity<SubtaskResponseDTO> getSubtaskById(
            @Parameter(description = "ID da subtarefa") @PathVariable UUID subtaskId) {
        return ResponseEntity.ok(subtaskService.getSubtaskById(subtaskId));
    }

    @PatchMapping("/subtasks/{subtaskId}/status")
    @Operation(summary = "Atualizar status da subtarefa", description = "Atualiza o status de uma subtarefa específica")
    public ResponseEntity<SubtaskResponseDTO> updateSubtaskStatus(
            @Parameter(description = "ID da subtarefa") @PathVariable UUID subtaskId,
            @Valid @RequestBody UpdateSubtaskStatusDTO updateSubtaskStatusDTO) {
        return ResponseEntity.ok(subtaskService.updateSubtaskStatus(subtaskId, updateSubtaskStatusDTO));
    }
} 