package com.ipaas.taskmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ipaas.taskmanager.domain.entity.User;
import com.ipaas.taskmanager.dto.request.CreateUserDTO;
import com.ipaas.taskmanager.dto.request.UpdateUserDTO;
import com.ipaas.taskmanager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private User testUser;
    private UUID userId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        testUser = User.builder()
                .name("Test User")
                .email("test@email.com")
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();
        testUser = userRepository.save(testUser);
        userId = testUser.getId();
    }

    @Test
    void createUser_ShouldCreateUserSuccessfully() throws Exception {
        CreateUserDTO createUserDTO = CreateUserDTO.builder()
                .name("New User")
                .email("newuser@email.com")
                .build();

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New User"))
                .andExpect(jsonPath("$.email").value("newuser@email.com"))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void createUser_ShouldReturnBadRequest_WhenNameIsEmpty() throws Exception {
        CreateUserDTO createUserDTO = CreateUserDTO.builder()
                .name("")
                .email("test@email.com")
                .build();

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_ShouldReturnBadRequest_WhenEmailIsInvalid() throws Exception {
        CreateUserDTO createUserDTO = CreateUserDTO.builder()
                .name("Test User")
                .email("invalid-email")
                .build();

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_ShouldReturnConflict_WhenEmailAlreadyExists() throws Exception {
        CreateUserDTO createUserDTO = CreateUserDTO.builder()
                .name("Another User")
                .email("test@email.com")
                .build();

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    void getUserById_ShouldReturnUser() throws Exception {
        mockMvc.perform(get("/api/v1/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@email.com"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void getUserById_ShouldReturnNotFound_WhenUserDoesNotExist() throws Exception {
        UUID nonExistentUserId = UUID.randomUUID();

        mockMvc.perform(get("/api/v1/users/{userId}", nonExistentUserId))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateUser_ShouldUpdateUserSuccessfully() throws Exception {
        UpdateUserDTO updateUserDTO = UpdateUserDTO.builder()
                .name("Updated User")
                .email("updated@email.com")
                .build();

        mockMvc.perform(patch("/api/v1/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated User"))
                .andExpect(jsonPath("$.email").value("updated@email.com"));
    }

    @Test
    void updateUser_ShouldReturnNotFound_WhenUserDoesNotExist() throws Exception {
        UUID nonExistentUserId = UUID.randomUUID();
        UpdateUserDTO updateUserDTO = UpdateUserDTO.builder()
                .name("Updated User")
                .email("updated@email.com")
                .build();

        mockMvc.perform(patch("/api/v1/users/{userId}", nonExistentUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateUser_ShouldReturnConflict_WhenEmailAlreadyExists() throws Exception {
        User anotherUser = User.builder()
                .name("Another User")
                .email("another@email.com")
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();
        userRepository.save(anotherUser);

        UpdateUserDTO updateUserDTO = UpdateUserDTO.builder()
                .name("Updated User")
                .email("another@email.com")
                .build();

        mockMvc.perform(patch("/api/v1/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    void deleteUser_ShouldDeleteUserSuccessfully() throws Exception {
        mockMvc.perform(delete("/api/v1/users/{userId}", userId))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUser_ShouldReturnNotFound_WhenUserDoesNotExist() throws Exception {
        UUID nonExistentUserId = UUID.randomUUID();

        mockMvc.perform(delete("/api/v1/users/{userId}", nonExistentUserId))
                .andExpect(status().isNotFound());
    }
} 