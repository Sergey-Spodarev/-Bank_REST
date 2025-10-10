package com.example.bankcards.controller;

import com.example.bankcards.dto.LoginUserDTO;
import com.example.bankcards.dto.UserDTO;
import com.example.bankcards.entity.Role;
import com.example.bankcards.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Management", description = "APIs for managing users")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create user", description = "ADMIN only - Create a new user")
    @ApiResponse(responseCode = "201", description = "User created successfully")
    @ApiResponse(responseCode = "400", description = "Validation error or username already taken")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody LoginUserDTO user){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.createUser(user));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all users", description = "ADMIN only - Get all users with pagination")
    public ResponseEntity<Page<UserDTO>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<UserDTO> users = userService.findAllUsers(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get user by ID", description = "ADMIN only - Get user by ID")
    @ApiResponse(responseCode = "200", description = "User found")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long userId){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.findUserByUserId(userId));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get current user", description = "Get authenticated user's information")
    public ResponseEntity<UserDTO> getCurrentUser(Authentication auth) {
        UserDTO userDTO = userService.getCurrentUser(auth);
        return ResponseEntity.ok(userDTO);
    }

    @PatchMapping("/{userId}/role")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update user role", description = "ADMIN only - Update user role")
    @ApiResponse(responseCode = "200", description = "Role updated successfully")
    @ApiResponse(responseCode = "400", description = "Cannot change your own role")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<UserDTO> updateUserRole(
            @PathVariable Long userId,
            @RequestBody Role newRole,
            Authentication auth) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.updateUserRole(userId, newRole, auth));
    }

    @PatchMapping("/me/username")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update username", description = "Update current user's username")
    @ApiResponse(responseCode = "200", description = "Username updated successfully")
    @ApiResponse(responseCode = "400", description = "Username already taken or same as current")
    public ResponseEntity<UserDTO> updateUsername(
            Authentication auth,
            @RequestParam @jakarta.validation.constraints.NotBlank
            @jakarta.validation.constraints.Size(min = 3, max = 50) String newUsername) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.updateUsername(auth, newUsername));
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete user", description = "ADMIN only - Delete user by ID")
    @ApiResponse(responseCode = "204", description = "User deleted successfully")
    @ApiResponse(responseCode = "400", description = "Cannot delete yourself")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long userId,
            Authentication auth) {

        userService.deleteUser(userId, auth);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all users (no pagination)", description = "ADMIN only - Get all users without pagination")
    @Deprecated
    public ResponseEntity<List<UserDTO>> getAllUsersWithoutPagination(){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.findAllUsersList());
    }
}