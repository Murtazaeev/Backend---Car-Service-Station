package com.CarServieStation.backend.controller;


import com.CarServieStation.backend.dto.ChangePasswordRequest;
import com.CarServieStation.backend.dto.RegisterRequest;
import com.CarServieStation.backend.dto.UpdateUserRequest;
import com.CarServieStation.backend.dto.UserResponse;
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
            @RequestBody ChangePasswordRequest request,
            Principal connectedUser
    ) {
        service.changePassword(request, connectedUser);
        return ResponseEntity.ok().build();
    }
    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(service.createUser(request));
    }

    @GetMapping("/managers")
    public ResponseEntity<List<UserResponse>> getAllUnAssignedManagers() {
        List<UserResponse> employees = service.getAllUnAssignedManagers();
        return ResponseEntity.ok(employees);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer userId) {
        service.deleteUser(userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Integer userId, @RequestBody UpdateUserRequest request) {
        UserResponse updatedUser = service.updateUser(userId, request);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getCurrentUserProfile(Principal principal) {
        return ResponseEntity.ok(service.findUserByEmail(principal.getName()));
    }

    @GetMapping("/managers/all")
    public ResponseEntity<List<UserResponse>> getAllManagers() {
        List<UserResponse> managers = service.getAllManagers();
        return ResponseEntity.ok(managers);
    }
}
