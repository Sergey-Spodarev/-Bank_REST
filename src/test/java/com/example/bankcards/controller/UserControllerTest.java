package com.example.bankcards.controller;

import com.example.bankcards.dto.LoginUserDTO;
import com.example.bankcards.dto.UserDTO;
import com.example.bankcards.entity.Role;
import com.example.bankcards.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserController userController;

    @Test
    void createUser_ShouldReturnCreated() {
        // Given
        LoginUserDTO loginUserDTO = new LoginUserDTO();
        loginUserDTO.setUsername("newuser");
        loginUserDTO.setPassword("password123");
        loginUserDTO.setRole(Role.USER);

        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setUsername("newuser");
        userDTO.setRole(Role.USER);

        when(userService.createUser(any(LoginUserDTO.class))).thenReturn(userDTO);

        // When
        ResponseEntity<UserDTO> response = userController.createUser(loginUserDTO);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("newuser", response.getBody().getUsername());
        verify(userService).createUser(loginUserDTO);
    }

    @Test
    void getAllUsers_ShouldReturnPage() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        UserDTO user1 = new UserDTO();
        user1.setId(1L);
        user1.setUsername("user1");
        UserDTO user2 = new UserDTO();
        user2.setId(2L);
        user2.setUsername("user2");

        Page<UserDTO> page = new PageImpl<>(List.of(user1, user2), pageable, 2);

        when(userService.findAllUsers(any(Pageable.class))).thenReturn(page);

        // When
        ResponseEntity<Page<UserDTO>> response = userController.getAllUsers(0, 10);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().getTotalElements());
        verify(userService).findAllUsers(pageable);
    }

    @Test
    void getUser_ShouldReturnUser() {
        // Given
        Long userId = 1L;
        UserDTO userDTO = new UserDTO();
        userDTO.setId(userId);
        userDTO.setUsername("testuser");
        userDTO.setRole(Role.USER);

        when(userService.findUserByUserId(userId)).thenReturn(userDTO);

        // When
        ResponseEntity<UserDTO> response = userController.getUser(userId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(userId, response.getBody().getId());
        assertEquals("testuser", response.getBody().getUsername());
        verify(userService).findUserByUserId(userId);
    }

    @Test
    void getCurrentUser_ShouldReturnCurrentUser() {
        // Given
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setUsername("currentuser");
        userDTO.setRole(Role.USER);

        when(authentication.getName()).thenReturn("currentuser");
        when(userService.getCurrentUser(authentication)).thenReturn(userDTO);

        // When
        ResponseEntity<UserDTO> response = userController.getCurrentUser(authentication);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("currentuser", response.getBody().getUsername());
        verify(userService).getCurrentUser(authentication);
    }

    @Test
    void updateUserRole_ShouldReturnUpdatedUser() {
        // Given
        Long userId = 2L;
        Role newRole = Role.ADMIN;
        UserDTO userDTO = new UserDTO();
        userDTO.setId(userId);
        userDTO.setUsername("updateduser");
        userDTO.setRole(newRole);

        when(userService.updateUserRole(eq(userId), eq(newRole), any(Authentication.class))).thenReturn(userDTO);

        // When
        ResponseEntity<UserDTO> response = userController.updateUserRole(userId, newRole, authentication);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(Role.ADMIN, response.getBody().getRole());
        verify(userService).updateUserRole(userId, newRole, authentication);
    }

    @Test
    void updateUsername_ShouldReturnUpdatedUser() {
        // Given
        String newUsername = "newusername";
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setUsername(newUsername);
        userDTO.setRole(Role.USER);

        when(userService.updateUsername(any(Authentication.class), eq(newUsername))).thenReturn(userDTO);

        // When
        ResponseEntity<UserDTO> response = userController.updateUsername(authentication, newUsername);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(newUsername, response.getBody().getUsername());
        verify(userService).updateUsername(authentication, newUsername);
    }

    @Test
    void deleteUser_ShouldReturnNoContent() {
        // Given
        Long userId = 2L;
        doNothing().when(userService).deleteUser(eq(userId), any(Authentication.class));

        // When
        ResponseEntity<Void> response = userController.deleteUser(userId, authentication);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService).deleteUser(userId, authentication);
    }

    @Test
    void getAllUsersWithoutPagination_ShouldReturnList() {
        // Given
        UserDTO user1 = new UserDTO();
        user1.setId(1L);
        UserDTO user2 = new UserDTO();
        user2.setId(2L);
        List<UserDTO> users = List.of(user1, user2);

        when(userService.findAllUsersList()).thenReturn(users);

        // When
        ResponseEntity<List<UserDTO>> response = userController.getAllUsersWithoutPagination();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(userService).findAllUsersList();
    }
}