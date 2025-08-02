package com.ipaas.taskmanager.exception.user;

public class UserInactiveException extends RuntimeException {
    public UserInactiveException(String message) {
        super(message);
    }
} 