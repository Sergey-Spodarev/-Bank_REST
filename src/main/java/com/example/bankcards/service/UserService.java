package com.example.bankcards.service;

import com.example.bankcards.dto.LoginUserDTO;
import com.example.bankcards.dto.UserDTO;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.SameUsernameException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.exception.UsernameAlreadyTakenException;
import com.example.bankcards.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        User user = new User();
        user.setUsername(loginUser.getUsername());
        user.setPassword(passwordEncoder.encode(loginUser.getPassword()));
        user.setRole(loginUser.getRole());
        return convertUserToDTO(userRepository.save(user));
    }

    public List<UserDTO> findAllUsers(){
        return userRepository.findAll().stream()
                .map(this::convertUserToDTO)
                .toList();
    }

    public UserDTO findUserByUserId(Long userId){
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.byId(userId));
        return convertUserToDTO(findUser);
    }

    public UserDTO swapRoleToAdmin(Long userId, Role newRole){
        User searchUser = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.byId(userId));
        searchUser.setRole(newRole);
        userRepository.save(searchUser);
        return convertUserToDTO(searchUser);
    }

    public UserDTO swapUserName(Authentication auth, String newUsername){
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> UserNotFoundException.byUsername(auth.getName()));

        if(user.getUsername().equals(newUsername)){
            throw new SameUsernameException(newUsername);
        }

        if (userRepository.existsByUsername(newUsername)) {
            throw new UsernameAlreadyTakenException(newUsername);
        }

        user.setUsername(newUsername);
        return convertUserToDTO(userRepository.save(user));
    }

    public void deleteUser(Long userId){
        User searchUser = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.byId(userId));
        userRepository.delete(searchUser);
    }

    private UserDTO convertUserToDTO(User user){
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setRole(user.getRole());
        return userDTO;
    }
}
