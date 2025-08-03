package com.ipaas.taskmanager.exception.task;

public class TaskCannotBeCompletedException extends RuntimeException {
    
    public TaskCannotBeCompletedException(String message) {
        super(message);
    }
} 