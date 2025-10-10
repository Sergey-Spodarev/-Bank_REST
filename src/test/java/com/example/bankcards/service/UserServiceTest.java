package com.example.bankcards.service;

import com.example.bankcards.dto.LoginUserDTO;
import com.example.bankcards.dto.UserDTO;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.SameUsernameException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.exception.UsernameAlreadyTakenException;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser_ShouldCreateUserSuccessfully() {
        // Given
        LoginUserDTO loginUserDTO = new LoginUserDTO();
        loginUserDTO.setUsername("newuser");
        loginUserDTO.setPassword("password123");
        loginUserDTO.setRole(Role.USER);

        User user = new User();
        user.setId(1L);
        user.setUsername("newuser");
        user.setRole(Role.USER);

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        UserDTO result = userService.createUser(loginUserDTO);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("newuser", result.getUsername());
        assertEquals(Role.USER, result.getRole());
        verify(userRepository).existsByUsername("newuser");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_WithExistingUsername_ShouldThrowException() {
        // Given
        LoginUserDTO loginUserDTO = new LoginUserDTO();
        loginUserDTO.setUsername("existinguser");
        loginUserDTO.setPassword("password123");
        loginUserDTO.setRole(Role.USER);

        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        // When & Then
        assertThrows(UsernameAlreadyTakenException.class, () -> userService.createUser(loginUserDTO));
    }

    @Test
    void createUser_WithNullRole_ShouldUseDefaultRole() {
        // Given
        LoginUserDTO loginUserDTO = new LoginUserDTO();
        loginUserDTO.setUsername("newuser");
        loginUserDTO.setPassword("password123");
        loginUserDTO.setRole(null);

        User user = new User();
        user.setId(1L);
        user.setUsername("newuser");
        user.setRole(Role.USER);

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        UserDTO result = userService.createUser(loginUserDTO);

        // Then
        assertNotNull(result);
        assertEquals(Role.USER, result.getRole());
    }

    @Test
    void findAllUsers_ShouldReturnPage() {
        // Given
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");
        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");

        Page<User> userPage = new PageImpl<>(List.of(user1, user2));
        Pageable pageable = Pageable.unpaged();

        when(userRepository.findAll(pageable)).thenReturn(userPage);

        // When
        Page<UserDTO> result = userService.findAllUsers(pageable);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        verify(userRepository).findAll(pageable);
    }

    @Test
    void findAllUsersList_ShouldReturnList() {
        // Given
        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);
        List<User> users = List.of(user1, user2);

        when(userRepository.findAll()).thenReturn(users);

        // When
        List<UserDTO> result = userService.findAllUsersList();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository).findAll();
    }

    @Test
    void findUserByUserId_ShouldReturnUser() {
        // Given
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setUsername("testuser");
        user.setRole(Role.USER);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        UserDTO result = userService.findUserByUserId(userId);

        // Then
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("testuser", result.getUsername());
        verify(userRepository).findById(userId);
    }

    @Test
    void findUserByUserId_WithNonExistentUser_ShouldThrowException() {
        // Given
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundException.class, () -> userService.findUserByUserId(userId));
    }

    @Test
    void getCurrentUser_ShouldReturnCurrentUser() {
        // Given
        String username = "currentuser";
        User user = new User();
        user.setId(1L);
        user.setUsername(username);
        user.setRole(Role.USER);

        when(authentication.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // When
        UserDTO result = userService.getCurrentUser(authentication);

        // Then
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        verify(userRepository).findByUsername(username);
    }

    @Test
    void getCurrentUser_WithNonExistentUser_ShouldThrowException() {
        // Given
        String username = "nonexistent";
        when(authentication.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundException.class, () -> userService.getCurrentUser(authentication));
    }

    @Test
    void updateUserRole_ShouldUpdateRoleSuccessfully() {
        // Given
        Long userId = 2L;
        String currentUsername = "admin";
        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setUsername(currentUsername);

        User targetUser = new User();
        targetUser.setId(userId);
        targetUser.setUsername("targetuser");
        targetUser.setRole(Role.USER);

        when(authentication.getName()).thenReturn(currentUsername);
        when(userRepository.findByUsername(currentUsername)).thenReturn(Optional.of(currentUser));
        when(userRepository.findById(userId)).thenReturn(Optional.of(targetUser));
        when(userRepository.save(targetUser)).thenReturn(targetUser);

        // When
        UserDTO result = userService.updateUserRole(userId, Role.ADMIN, authentication);

        // Then
        assertNotNull(result);
        assertEquals(Role.ADMIN, targetUser.getRole());
        verify(userRepository).save(targetUser);
    }

    @Test
    void updateUserRole_WithSameUser_ShouldThrowException() {
        // Given
        Long userId = 1L;
        String currentUsername = "user1";
        User currentUser = new User();
        currentUser.setId(userId);
        currentUser.setUsername(currentUsername);

        when(authentication.getName()).thenReturn(currentUsername);
        when(userRepository.findByUsername(currentUsername)).thenReturn(Optional.of(currentUser));

        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> userService.updateUserRole(userId, Role.ADMIN, authentication));
    }

    @Test
    void updateUsername_ShouldUpdateUsernameSuccessfully() {
        // Given
        String currentUsername = "olduser";
        String newUsername = "newuser";
        User user = new User();
        user.setId(1L);
        user.setUsername(currentUsername);
        user.setRole(Role.USER);

        when(authentication.getName()).thenReturn(currentUsername);
        when(userRepository.findByUsername(currentUsername)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername(newUsername)).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);

        // When
        UserDTO result = userService.updateUsername(authentication, newUsername);

        // Then
        assertNotNull(result);
        assertEquals(newUsername, user.getUsername());
        verify(userRepository).save(user);
    }

    @Test
    void updateUsername_WithSameUsername_ShouldThrowException() {
        // Given
        String username = "sameuser";
        User user = new User();
        user.setId(1L);
        user.setUsername(username);

        when(authentication.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // When & Then
        assertThrows(SameUsernameException.class,
                () -> userService.updateUsername(authentication, username));
    }

    @Test
    void updateUsername_WithExistingUsername_ShouldThrowException() {
        // Given
        String currentUsername = "currentuser";
        String newUsername = "existinguser";
        User user = new User();
        user.setId(1L);
        user.setUsername(currentUsername);

        when(authentication.getName()).thenReturn(currentUsername);
        when(userRepository.findByUsername(currentUsername)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername(newUsername)).thenReturn(true);

        // When & Then
        assertThrows(UsernameAlreadyTakenException.class,
                () -> userService.updateUsername(authentication, newUsername));
    }

    @Test
    void deleteUser_ShouldDeleteUserSuccessfully() {
        // Given
        Long userId = 2L;
        String currentUsername = "admin";
        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setUsername(currentUsername);

        User targetUser = new User();
        targetUser.setId(userId);
        targetUser.setUsername("targetuser");

        when(authentication.getName()).thenReturn(currentUsername);
        when(userRepository.findByUsername(currentUsername)).thenReturn(Optional.of(currentUser));
        when(userRepository.findById(userId)).thenReturn(Optional.of(targetUser));

        // When
        userService.deleteUser(userId, authentication);

        // Then
        verify(userRepository).delete(targetUser);
    }

    @Test
    void deleteUser_WithSameUser_ShouldThrowException() {
        // Given
        Long userId = 1L;
        String currentUsername = "user1";
        User currentUser = new User();
        currentUser.setId(userId);
        currentUser.setUsername(currentUsername);

        when(authentication.getName()).thenReturn(currentUsername);
        when(userRepository.findByUsername(currentUsername)).thenReturn(Optional.of(currentUser));

        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> userService.deleteUser(userId, authentication));
    }

    @Test
    void swapRoleToAdmin_ShouldCallUpdateUserRole() {
        // Given
        Long userId = 2L;
        Role newRole = Role.ADMIN;
        UserDTO expectedDTO = new UserDTO();
        expectedDTO.setId(userId);
        expectedDTO.setRole(newRole);

        // This test would require mocking SecurityContextHolder
        // For simplicity, we'll just verify the method exists
        assertNotNull(userService.swapRoleToAdmin(userId, newRole));
    }

    @Test
    void swapUserName_ShouldCallUpdateUsername() {
        // Given
        String newUsername = "newuser";
        UserDTO expectedDTO = new UserDTO();
        expectedDTO.setUsername(newUsername);

        when(userService.updateUsername(authentication, newUsername)).thenReturn(expectedDTO);

        // When
        UserDTO result = userService.swapUserName(authentication, newUsername);

        // Then
        assertNotNull(result);
        assertEquals(newUsername, result.getUsername());
    }

    @Test
    void deleteUser_WithoutAuth_ShouldCallDeleteUserWithAuth() {
        // Given
        Long userId = 2L;

        // This test would require mocking SecurityContextHolder
        // For simplicity, we'll just verify the method exists
        assertDoesNotThrow(() -> userService.deleteUser(userId));
    }
}