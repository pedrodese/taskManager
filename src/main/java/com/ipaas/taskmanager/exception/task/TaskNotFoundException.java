package com.ipaas.taskmanager.exception.task;

public class TaskNotFoundException extends RuntimeException {
    
    public TaskNotFoundException(String message) {
        super(message);
    }
} 