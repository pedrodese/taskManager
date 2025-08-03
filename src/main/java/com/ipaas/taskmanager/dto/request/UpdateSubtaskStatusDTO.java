package com.ipaas.taskmanager.dto.request;

import com.ipaas.taskmanager.domain.enums.TaskStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSubtaskStatusDTO {

    @NotNull(message = "Status is required")
    private TaskStatus status;
} 