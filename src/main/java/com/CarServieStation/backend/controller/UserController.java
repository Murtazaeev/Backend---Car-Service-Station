package com.CarServieStation.backend.controller;


import com.CarServieStation.backend.dto.ChangePasswordRequestDto;
import com.CarServieStation.backend.dto.RegisterRequestDto;
import com.CarServieStation.backend.dto.UpdateUserRequestDto;
import com.CarServieStation.backend.dto.UserResponseDto;
import com.CarServieStation.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.security.Principal;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PatchMapping
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordRequestDto request,
            Principal connectedUser
    ) {
        service.changePassword(request, connectedUser);
        return ResponseEntity.ok().build();
    }
    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@RequestBody RegisterRequestDto request) {
        UserResponseDto newUserResponseDto = service.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUserResponseDto);
    }

    @GetMapping("/managers")
    public ResponseEntity<List<UserResponseDto>> getAllUnAssignedManagers() {
        List<UserResponseDto> employees = service.getAllUnAssignedManagers();
        return ResponseEntity.ok(employees);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer userId) {
        service.deleteUser(userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable Integer userId, @RequestBody UpdateUserRequestDto request) {
        UserResponseDto updatedUser = service.updateUser(userId, request);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserResponseDto> getCurrentUserProfile(Principal principal) {
        return ResponseEntity.ok(service.findUserByEmail(principal.getName()));
    }

    @GetMapping("/managers/all")
    public ResponseEntity<List<UserResponseDto>> getAllManagers() {
        List<UserResponseDto> managers = service.getAllManagers();
        return ResponseEntity.ok(managers);
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserResponseDto>> searchUsers(@RequestParam String name) {
        List<UserResponseDto> users = service.searchUsersByFirstNameOrLastName(name);
        return ResponseEntity.ok(users);
    }
}
