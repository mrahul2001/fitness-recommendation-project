package com.pm.usersservice.service;

import com.pm.usersservice.dto.UserRequestDTO;
import com.pm.usersservice.dto.UserResponseDTO;
import com.pm.usersservice.exception.EmailAlreadyExistsException;
import com.pm.usersservice.exception.UserNotFoundException;
import com.pm.usersservice.model.User;
import com.pm.usersservice.model.UserRole;
import com.pm.usersservice.respository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsersService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserResponseDTO> getAllUsers() {
        List<User> users = usersRepository.findAll();
        List<UserResponseDTO> userResponseDTOS = new ArrayList<>();
        for (User user : users) {
            UserResponseDTO userResponseDTO = UserModelToUserResponseDTO(user);
            userResponseDTOS.add(userResponseDTO);
        }

        return userResponseDTOS;
    }

    public UserResponseDTO getUser(UUID userID) {
        User user = usersRepository.findById(userID)
                .orElseThrow(() -> new UserNotFoundException(userID.toString()));

        return UserModelToUserResponseDTO(user);
    }

    public UserResponseDTO registerUser(UserRequestDTO body) {

        if (usersRepository.existsByEmail(body.getEmail())) {
            throw new EmailAlreadyExistsException("Email " + body.getEmail() + " already exists");
        }

        User savedUser = usersRepository.save(UserRequestDTOToUser(body));
        return UserModelToUserResponseDTO(savedUser);
    }

    public UserResponseDTO updateUser(UUID userID, UserRequestDTO body) {
        User user = usersRepository.findById(userID)
                .orElseThrow(() -> new UserNotFoundException(userID.toString()));

        user.setEmail(body.getEmail());
        user.setFirstName(body.getFirstName());
        user.setLastName(body.getLastName());
        user.setPassword(passwordEncoder.encode(body.getPassword()));
        user.setRole(body.getUserRole());

        User savedUser = usersRepository.save(user);
        return UserModelToUserResponseDTO(savedUser);
    }


    public UserResponseDTO UserModelToUserResponseDTO(User user) {
        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(user.getId());
        userResponseDTO.setEmail(user.getEmail());
        userResponseDTO.setFirstName(user.getFirstName());
        userResponseDTO.setLastName(user.getLastName());
        userResponseDTO.setUserRole(
                user.getRole() != null
                ? user.getRole()
                : UserRole.USER);
        userResponseDTO.setCreatedTime(user.getCreatedTime());
        userResponseDTO.setUpdatedTime(user.getUpdatedTime());

        return userResponseDTO;
    }

    public User UserRequestDTOToUser(UserRequestDTO userRequestDTO) {
        User user = new User();
        user.setEmail(userRequestDTO.getEmail());
        user.setFirstName(userRequestDTO.getFirstName());
        user.setLastName(userRequestDTO.getLastName());
        user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
        user.setRole(
                userRequestDTO.getUserRole() != null
                        ? userRequestDTO.getUserRole()
                        : UserRole.USER
        );

        return user;
    }

    public String deleteUser(UUID userID) {
        User user = usersRepository.findById(userID)
                        .orElseThrow(() -> new UserNotFoundException(userID.toString()));

        usersRepository.deleteById(userID);

        return "User has been deleted";
    }
}
