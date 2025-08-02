package com.ipaas.taskmanager.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ipaas.taskmanager.domain.entity.User;
import com.ipaas.taskmanager.dto.request.CreateUserDTO;
import com.ipaas.taskmanager.dto.request.UpdateUserDTO;
import com.ipaas.taskmanager.dto.response.UserResponseDTO;
import com.ipaas.taskmanager.exception.user.UserAlreadyExistsException;
import com.ipaas.taskmanager.exception.user.UserInactiveException;
import com.ipaas.taskmanager.exception.user.UserNotFoundException;
import com.ipaas.taskmanager.mapper.UserMapper;
import com.ipaas.taskmanager.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserResponseDTO createUser(CreateUserDTO createUserDTO) {
        log.info("Iniciando criação de usuário com email: {}", createUserDTO.getEmail());
        
        if(userRepository.existsByEmail(createUserDTO.getEmail())) {
            log.warn("Tentativa de criar usuário com email já existente: {}", createUserDTO.getEmail());
            throw new UserAlreadyExistsException("Already exists a user with this email: " + createUserDTO.getEmail());
        }
        
        User user = userMapper.toEntity(createUserDTO);
        User savedUser = userRepository.save(user);
        
        log.info("Usuário criado com sucesso. ID: {}, Email: {}", savedUser.getId(), savedUser.getEmail());
        return userMapper.toDTO(savedUser);
    }

    public UserResponseDTO getUserById(UUID userId) {
        log.info("Buscando usuário com ID: {}", userId);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        
        if (!user.getActive()) {
            log.warn("Tentativa de acessar usuário inativo. ID: {}", userId);
            throw new UserInactiveException("This user is inactive: " + userId);
        }
        
        log.info("Usuário encontrado com sucesso. ID: {}, Email: {}", user.getId(), user.getEmail());
        return userMapper.toDTO(user);
    }

    public UserResponseDTO updateUser(UUID userId, UpdateUserDTO updateUserDTO) {
        log.info("Atualizando usuário com ID: {}", userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        
        if (updateUserDTO.getEmail() != null && !updateUserDTO.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(updateUserDTO.getEmail())) {
                log.warn("Tentativa de atualizar usuário com email já existente: {}", updateUserDTO.getEmail());
                throw new UserAlreadyExistsException("Already exists a user with this email: " + updateUserDTO.getEmail());
            }
        }
        
        // Atualiza os campos do usuário
        if (updateUserDTO.getName() != null && !updateUserDTO.getName().trim().isEmpty()) {
            user.setName(updateUserDTO.getName());
        }
        if (updateUserDTO.getEmail() != null && !updateUserDTO.getEmail().trim().isEmpty()) {
            user.setEmail(updateUserDTO.getEmail());
        }
        
        User updatedUser = userRepository.save(user);
        
        log.info("Usuário atualizado com sucesso. ID: {}, Email: {}", updatedUser.getId(), updatedUser.getEmail());
        return userMapper.toDTO(updatedUser);
    }

    public void deleteUser(UUID userId) {
        log.info("Iniciando soft delete do usuário com ID: {}", userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        
        if (!user.getActive()) {
            log.warn("Tentativa de deletar usuário já inativo. ID: {}", userId);
            throw new IllegalStateException("This user is inactive: " + userId);
        }
        
        user.setActive(false);
        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);
        
        log.info("Usuário deletado com sucesso (soft delete). ID: {}, Email: {}", user.getId(), user.getEmail());
    }
}
