package com.example.bankcards.service;

import com.example.bankcards.dto.LoginUserDTO;
import com.example.bankcards.dto.UserDTO;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.SameUsernameException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.exception.UsernameAlreadyTakenException;
import com.example.bankcards.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDTO createUser(LoginUserDTO loginUser){
        if (userRepository.existsByUsername(loginUser.getUsername())) {
            throw new UsernameAlreadyTakenException(loginUser.getUsername());
        }

        User user = new User();
        user.setUsername(loginUser.getUsername().trim());
        user.setPassword(passwordEncoder.encode(loginUser.getPassword()));
        user.setRole(loginUser.getRole() != null ? loginUser.getRole() : Role.USER);

        User savedUser = userRepository.save(user);
        log.info("User created successfully: {}", user.getUsername());
        return convertUserToDTO(savedUser);
    }

    public Page<UserDTO> findAllUsers(Pageable pageable){
        return userRepository.findAll(pageable)
                .map(this::convertUserToDTO);
    }

    public List<UserDTO> findAllUsersList(){
        return userRepository.findAll().stream()
                .map(this::convertUserToDTO)
                .toList();
    }

    public UserDTO findUserByUserId(Long userId){
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.byId(userId));
        return convertUserToDTO(findUser);
    }

    public UserDTO getCurrentUser(Authentication auth) {
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> UserNotFoundException.byUsername(auth.getName()));
        return convertUserToDTO(user);
    }

    public UserDTO updateUserRole(Long userId, Role newRole, Authentication auth){
        String currentUsername = auth.getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> UserNotFoundException.byUsername(currentUsername));

        if (currentUser.getId().equals(userId)) {
            throw new IllegalArgumentException("Cannot change your own role");
        }

        User searchUser = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.byId(userId));

        searchUser.setRole(newRole);
        User updatedUser = userRepository.save(searchUser);

        log.info("User {} role changed to {} by {}", userId, newRole, currentUsername);
        return convertUserToDTO(updatedUser);
    }

    public UserDTO updateUsername(Authentication auth, String newUsername){
        String trimmedUsername = newUsername.trim();
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> UserNotFoundException.byUsername(auth.getName()));

        if(user.getUsername().equals(trimmedUsername)){
            throw new SameUsernameException(trimmedUsername);
        }

        if (userRepository.existsByUsername(trimmedUsername)) {
            throw new UsernameAlreadyTakenException(trimmedUsername);
        }

        String oldUsername = user.getUsername();
        user.setUsername(trimmedUsername);
        User updatedUser = userRepository.save(user);

        log.info("Username changed from {} to {}", oldUsername, trimmedUsername);
        return convertUserToDTO(updatedUser);
    }

    public void deleteUser(Long userId, Authentication auth){
        String currentUsername = auth.getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> UserNotFoundException.byUsername(currentUsername));

        if (currentUser.getId().equals(userId)) {
            throw new IllegalArgumentException("Cannot delete yourself");
        }

        User searchUser = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.byId(userId));

        userRepository.delete(searchUser);
        log.info("User {} deleted by {}", userId, currentUsername);
    }

    public UserDTO swapRoleToAdmin(Long userId, Role newRole) {
        Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        return updateUserRole(userId, newRole, auth);
    }

    public UserDTO swapUserName(Authentication auth, String newUsername) {
        return updateUsername(auth, newUsername);
    }

    public void deleteUser(Long userId) {
        Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        deleteUser(userId, auth);
    }

    private UserDTO convertUserToDTO(User user){
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setRole(user.getRole());
        return userDTO;
    }
}