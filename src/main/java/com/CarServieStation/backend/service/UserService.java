package com.CarServieStation.backend.service;


import com.CarServieStation.backend.dto.ChangePasswordRequest;
import com.CarServieStation.backend.dto.RegisterRequest;
import com.CarServieStation.backend.dto.UserResponse;
import com.CarServieStation.backend.entity.User;
import com.CarServieStation.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;
import static com.CarServieStation.backend.entity.Role.EMPLOYEE;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository repository;
    public void changePassword(ChangePasswordRequest request, Principal connectedUser) {

        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        // check if the current password is correct
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalStateException("Wrong password");
        }
        // check if the two new passwords are the same
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new IllegalStateException("Password are not the same");
        }

        // update the password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        // save the new password
        repository.save(user);
    }
    public String createUser(RegisterRequest request) {
        var dbUser = repository.findByEmail(request.getEmail());

        if (dbUser.isPresent()) {
            throw new RuntimeException("User already exists in the database!");
        }

        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .phoneNumber(request.getPhoneNumber())
                .birthDate(request.getBirthDate())
                .salary(request.getSalary())
                .totalOrders(request.getTotalOrders())
                .build();

        repository.save(user);
        return "Employee created successfully";
    }

    public void deleteUser(Integer userId) {
        repository.findById(userId).orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        repository.deleteById(userId);
    }

    public UserResponse updateUser(Integer userId, RegisterRequest updateRequest) {
        User user = repository.findById(userId).orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Update fields
        user.setFirstname(updateRequest.getFirstname());
        user.setLastname(updateRequest.getLastname());
        user.setRole(updateRequest.getRole());
        user.setPhoneNumber(updateRequest.getPhoneNumber());
        user.setBirthDate(updateRequest.getBirthDate());
        user.setSalary(updateRequest.getSalary());
        user.setTotalOrders(updateRequest.getTotalOrders());

        // Save the updated user
        repository.save(user);

        // Convert updated user to DTO
        return convertToDto(user);
    }


    public List<UserResponse> getAllEmployees() {
        return repository.findByRole(EMPLOYEE).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private UserResponse convertToDto(User user) {
        UserResponse dto = new UserResponse();
        dto.setId(user.getId());
        dto.setFirstname(user.getFirstname());
        dto.setLastname(user.getLastname());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole().toString());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setBirthDate(user.getBirthDate());
        dto.setSalary(user.getSalary());
        dto.setTotalOrders(user.getTotalOrders());
        return dto;
    }

}
