package com.ipaas.taskmanager.repository;

import com.ipaas.taskmanager.domain.entity.Subtask;
import com.ipaas.taskmanager.domain.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SubtaskRepository extends JpaRepository<Subtask, UUID> {

    List<Subtask> findByTaskId(UUID taskId);

    List<Subtask> findByTaskIdAndStatus(UUID taskId, TaskStatus status);

    long countByTaskIdAndStatus(UUID taskId, TaskStatus status);

    default boolean areAllSubtasksCompleted(UUID taskId) {
        return countByTaskIdAndStatus(taskId, TaskStatus.COMPLETED) == 
               countByTaskId(taskId);
    }

    long countByTaskId(UUID taskId);
} 