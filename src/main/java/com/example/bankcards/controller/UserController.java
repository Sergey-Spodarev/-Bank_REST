package com.example.bankcards.controller;

import com.example.bankcards.dto.LoginUserDTO;
import com.example.bankcards.dto.UserDTO;
import com.example.bankcards.entity.Role;
import com.example.bankcards.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> createUser(@RequestBody LoginUserDTO user){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.createUser(user));
    }

    @GetMapping("/allUsers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers(){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.findAllUsers());
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long userId){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.findUserByUserId(userId));
    }

    @PatchMapping("/{userId}/swapRole")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> updateUserRole(@PathVariable Long userId, @RequestBody Role newRole){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.swapRoleToAdmin(userId, newRole));
    }

    @PatchMapping("/me/swapUserName")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> swapUserName(Authentication auth, String newUsername){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.swapUserName(auth, newUsername));
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId){
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
//наверно надо ещё сделать поиск по роли и по имени
//todo подправить end-поинты и проверить на работоспасобность, после чего прописать тесты