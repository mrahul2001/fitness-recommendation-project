package com.pm.usersservice.service;

import com.pm.usersservice.dto.FindOrCreateRequestDTO;
import com.pm.usersservice.dto.UserRequestDTO;
import com.pm.usersservice.dto.UserResponseDTO;
import com.pm.usersservice.exception.EmailAlreadyExistsException;
import com.pm.usersservice.exception.UserNotFoundException;
import com.pm.usersservice.model.AuthProvider;
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
        List<UserResponseDTO> userResponseDTOs = new ArrayList<>();
        for (User user : users) {
            UserResponseDTO userResponseDTO = UserModelToUserResponseDTO(user);
            userResponseDTOs.add(userResponseDTO);
        }

        return userResponseDTOs;
    }

    public UserResponseDTO getUser(UUID userID) {
        User user = usersRepository.findById(userID)
                .orElseThrow(() -> new UserNotFoundException(userID.toString()));

        return UserModelToUserResponseDTO(user);
    }

    public UserResponseDTO registerUser(UserRequestDTO body) {
        Optional<User> user = usersRepository.findByEmail(body.getEmail());

        if (user.isPresent()) {
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

    public String deleteUser(UUID userID) {
        User user = usersRepository.findById(userID)
                .orElseThrow(() -> new UserNotFoundException(userID.toString()));

        usersRepository.deleteById(userID);

        return "User has been deleted";
    }

    public UserResponseDTO getUserByEmailId(String emailId) {
        User user = usersRepository.findByEmail(emailId)
                .orElseThrow(() -> new UserNotFoundException("User not found with email " + emailId));

        return UserModelToUserResponseDTO(user);
    }

    public UserResponseDTO findOrCreateProviderUser(FindOrCreateRequestDTO body) {
        Optional<User> existing = usersRepository.findByEmail(body.getEmail());
        if (existing.isPresent()) {
            User user = existing.get();

            if (user.getProviderId() == null) {
                user.setProviderId(body.getProviderId());
                usersRepository.save(user);
            }

            return UserModelToUserResponseDTO(user);
        }

        User newUser = new User();
        newUser.setEmail(body.getEmail());
        newUser.setFirstName(body.getFirstName());
        newUser.setLastName(body.getLastName());
        newUser.setAuthProvider(body.getAuthProvider());
        newUser.setProviderId(body.getProviderId());
        newUser.setPassword(null);
        newUser.setRole(UserRole.USER);

        usersRepository.save(newUser);
        return UserModelToUserResponseDTO(newUser);
    }


    public UserResponseDTO UserModelToUserResponseDTO(User user) {
        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(user.getId());
        userResponseDTO.setEmail(user.getEmail());
        userResponseDTO.setFirstName(user.getFirstName());
        userResponseDTO.setLastName(user.getLastName());
        userResponseDTO.setPassword(user.getPassword());
        userResponseDTO.setUserRole(user.getRole() != null ? user.getRole() : UserRole.USER);
        userResponseDTO.setAuthProvider(user.getAuthProvider() != null ? user.getAuthProvider() : AuthProvider.LOCALE);
        userResponseDTO.setProviderId(user.getProviderId());
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
        user.setRole(userRequestDTO.getUserRole() != null ? userRequestDTO.getUserRole() : UserRole.USER);
        user.setAuthProvider(userRequestDTO.getAuthProvider() != null ? userRequestDTO.getAuthProvider() : AuthProvider.LOCALE);
        user.setProviderId(userRequestDTO.getProviderId());

        return user;
    }

    public Boolean validateUser(UUID userID) {
        return usersRepository.existsById(userID);
    }
}
