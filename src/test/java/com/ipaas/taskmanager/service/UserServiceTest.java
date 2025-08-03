package com.ipaas.taskmanager.service;

import com.ipaas.taskmanager.domain.entity.User;
import com.ipaas.taskmanager.dto.request.CreateUserDTO;
import com.ipaas.taskmanager.dto.request.UpdateUserDTO;
import com.ipaas.taskmanager.dto.response.UserResponseDTO;
import com.ipaas.taskmanager.exception.user.UserAlreadyExistsException;
import com.ipaas.taskmanager.exception.user.UserNotFoundException;
import com.ipaas.taskmanager.mapper.UserMapper;
import com.ipaas.taskmanager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private UUID userId;
    private User user;
    private CreateUserDTO createUserDTO;
    private UpdateUserDTO updateUserDTO;
    private UserResponseDTO userResponseDTO;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        user = User.builder()
                .id(userId)
                .name("Test User")
                .email("test@email.com")
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();

        createUserDTO = CreateUserDTO.builder()
                .name("New User")
                .email("newuser@email.com")
                .build();

        updateUserDTO = UpdateUserDTO.builder()
                .name("Updated User")
                .email("updated@email.com")
                .build();

        userResponseDTO = UserResponseDTO.builder()
                .id(userId)
                .name("Test User")
                .email("test@email.com")
                .active(true)
                .build();
    }

    @Test
    void createUser_ShouldCreateUserSuccessfully() {
        when(userRepository.existsByEmail(createUserDTO.getEmail())).thenReturn(false);
        when(userMapper.toEntity(createUserDTO)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDTO(user)).thenReturn(userResponseDTO);

        UserResponseDTO result = userService.createUser(createUserDTO);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("Test User", result.getName());
        assertEquals("test@email.com", result.getEmail());
        assertTrue(result.getActive());
        verify(userRepository).existsByEmail(createUserDTO.getEmail());
        verify(userMapper).toEntity(createUserDTO);
        verify(userRepository).save(any(User.class));
        verify(userMapper).toDTO(user);
    }

    @Test
    void createUser_ShouldThrowException_WhenEmailAlreadyExists() {
        when(userRepository.existsByEmail(createUserDTO.getEmail())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> {
            userService.createUser(createUserDTO);
        });

        verify(userRepository).existsByEmail(createUserDTO.getEmail());
        verify(userRepository, never()).save(any());
    }

    @Test
    void getUserById_ShouldReturnUser() {
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toDTO(user)).thenReturn(userResponseDTO);

        UserResponseDTO result = userService.getUserById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("Test User", result.getName());
        verify(userRepository).findById(userId);
        verify(userMapper).toDTO(user);
    }

    @Test
    void getUserById_ShouldThrowException_WhenUserNotFound() {  
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.getUserById(userId);
        });

        verify(userRepository).findById(userId);
        verify(userMapper, never()).toDTO(any());
    }

    @Test
    void updateUser_ShouldUpdateUserSuccessfully() {
        User updatedUser = User.builder()
                .id(userId)
                .name("Updated User")
                .email("updated@email.com")
                .active(true)
                .build();

        UserResponseDTO updatedResponseDTO = UserResponseDTO.builder()
                .id(userId)
                .name("Updated User")
                .email("updated@email.com")
                .active(true)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(updateUserDTO.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userMapper.toDTO(updatedUser)).thenReturn(updatedResponseDTO);

        UserResponseDTO result = userService.updateUser(userId, updateUserDTO);

        assertNotNull(result);
        assertEquals("Updated User", result.getName());
        assertEquals("updated@email.com", result.getEmail());
        verify(userRepository).findById(userId);
        verify(userRepository).existsByEmail(updateUserDTO.getEmail());
        verify(userRepository).save(any(User.class));
        verify(userMapper).toDTO(updatedUser);
    }

    @Test
    void updateUser_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.updateUser(userId, updateUserDTO);
        });

        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_ShouldThrowException_WhenEmailAlreadyExists() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(updateUserDTO.getEmail())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> {
            userService.updateUser(userId, updateUserDTO);
        });

        verify(userRepository).findById(userId);
        verify(userRepository).existsByEmail(updateUserDTO.getEmail());
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteUser_ShouldDeleteUserSuccessfully() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.deleteUser(userId);

        verify(userRepository).findById(userId);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void deleteUser_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.deleteUser(userId);
        });

        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any());
    }
} 