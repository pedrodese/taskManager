package com.ipaas.taskmanager.exception.task;

public class InvalidTaskStatusTransitionException extends RuntimeException {
    
    public InvalidTaskStatusTransitionException(String message) {
        super(message);
    }
} 