package com.ipaas.taskmanager.mapper;

import org.springframework.stereotype.Component;

import com.ipaas.taskmanager.domain.entity.User;
import com.ipaas.taskmanager.dto.request.CreateUserDTO;
import com.ipaas.taskmanager.dto.response.UserResponseDTO;

@Component
public class UserMapper {

    public User toEntity(CreateUserDTO createUserDTO) {
        return User.builder()
            .name(createUserDTO.getName())
            .email(createUserDTO.getEmail())
            .build();
    }

    public UserResponseDTO toDTO(User user) {
        return UserResponseDTO.builder()
            .id(user.getId())
            .name(user.getName())
            .email(user.getEmail())
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt())
            .deletedAt(user.getDeletedAt())
            .active(user.getActive())
            .build();
    }


}
