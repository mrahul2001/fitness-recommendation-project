package com.pm.usersservice.controller;

import com.pm.usersservice.dto.UserRequestDTO;
import com.pm.usersservice.dto.UserResponseDTO;
import com.pm.usersservice.model.User;
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
}
