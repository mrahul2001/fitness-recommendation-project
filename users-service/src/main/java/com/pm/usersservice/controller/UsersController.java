package com.pm.usersservice.controller;

import com.pm.usersservice.dto.FindOrCreateRequestDTO;
import com.pm.usersservice.dto.LoginRequestDTO;
import com.pm.usersservice.dto.UserRequestDTO;
import com.pm.usersservice.dto.UserResponseDTO;
import com.pm.usersservice.service.UsersService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UsersController {
    private final UsersService usersService;

    @GetMapping("/")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(usersService.getAllUsers());
    }

    @GetMapping("/{userID}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable UUID userID) {
        return ResponseEntity.ok(usersService.getUser(userID));
    }

    @GetMapping("/{userID}/validate")
    public ResponseEntity<Boolean> validateUser(@PathVariable UUID userID) {
        return ResponseEntity.ok(usersService.validateUser(userID));
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody UserRequestDTO body) {
        return ResponseEntity.ok(usersService.registerUser(body));
    }

    @PutMapping("/{userID}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable UUID userID, @Valid @RequestBody UserRequestDTO body) {
        return ResponseEntity.ok(usersService.updateUser(userID, body));
    }

    @DeleteMapping("/{userID}")
    public ResponseEntity<String> deleteUser(@PathVariable UUID userID) {
        return ResponseEntity.ok(usersService.deleteUser(userID));
    }

    @GetMapping(value = "/email/{emailId}")
    public ResponseEntity<UserResponseDTO> getUserByEmailId(@PathVariable String emailId) {
        return ResponseEntity.ok(usersService.getUserByEmailId(emailId));
    }

    @PostMapping("/find-or-create")
    public ResponseEntity<UserResponseDTO> findOrCreateUser(@Valid @RequestBody FindOrCreateRequestDTO body) {
        return ResponseEntity.ok(usersService.findOrCreateProviderUser(body));
    }

    @PostMapping("/validate-login")
    public ResponseEntity<UserResponseDTO> validateLogin(@RequestBody LoginRequestDTO body) {

        return ResponseEntity.ok(usersService.validateLogin(body.getEmail(), body.getPassword()));
    }
}
