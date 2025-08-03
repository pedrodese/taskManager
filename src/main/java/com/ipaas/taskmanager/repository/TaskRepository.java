package com.ipaas.taskmanager.repository;

import com.ipaas.taskmanager.domain.entity.Task;
import com.ipaas.taskmanager.domain.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {

    @Query("SELECT t FROM Task t WHERE " +
           "(:userId IS NULL OR t.user.id = :userId) AND " +
           "(:status IS NULL OR t.status = :status)")
    Page<Task> findTasksWithFilters(
            @Param("userId") UUID userId,
            @Param("status") TaskStatus status,
            @Param("title") String title,
            Pageable pageable);
} 