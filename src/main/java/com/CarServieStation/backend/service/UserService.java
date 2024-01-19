package com.CarServieStation.backend.service;


import com.CarServieStation.backend.dto.ChangePasswordRequestDto;
import com.CarServieStation.backend.dto.RegisterRequestDto;
import com.CarServieStation.backend.dto.UpdateUserRequestDto;
import com.CarServieStation.backend.dto.UserResponseDto;
import com.CarServieStation.backend.entity.Station;
import com.CarServieStation.backend.entity.User;
import com.CarServieStation.backend.exception.NotFoundOrAlreadyExistException;
import com.CarServieStation.backend.repository.StationRepository;
import com.CarServieStation.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import static com.CarServieStation.backend.entity.Role.MANAGER;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository repository;
    private final StationRepository stationRepository;


    @Transactional
    public void changePassword(ChangePasswordRequestDto request, Principal connectedUser) {

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

    @Transactional
    public UserResponseDto createUser(RegisterRequestDto request) {
        var dbUser = repository.findByEmail(request.getEmail());

        if (dbUser.isPresent()) {
            throw new NotFoundOrAlreadyExistException("User already exists in the database!");
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

        User newUser = repository.save(user);
        return convertToDto(newUser);
    }


    @Transactional
    public void deleteUser(Integer userId) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new NotFoundOrAlreadyExistException("User not found with id: " + userId));

        if (user.getStation() != null) {
            Station station = user.getStation();
            station.setUser(null);
            stationRepository.save(station);
        }
        repository.deleteById(userId);
    }

    @Transactional
    public UserResponseDto updateUser(Integer userId, UpdateUserRequestDto updateRequest) {
        User user = repository.findById(userId).orElseThrow(() -> new NotFoundOrAlreadyExistException("User not found with id: " + userId));

        // Update fields
        user.setFirstname(updateRequest.getFirstname());
        user.setLastname(updateRequest.getLastname());
        user.setPhoneNumber(updateRequest.getPhoneNumber());
        user.setBirthDate(updateRequest.getBirthDate());
        user.setSalary(updateRequest.getSalary());
        user.setTotalOrders(updateRequest.getTotalOrders());

        // Save the updated user
        repository.save(user);

        // Convert updated user to DTO
        return convertToDto(user);
    }


    public List<UserResponseDto> getAllUnAssignedManagers() {
        return repository.findByRole(MANAGER).stream()
                .filter(user -> user.getStation() == null)
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<UserResponseDto> getAllManagers() {
        return repository.findByRole(MANAGER).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }


    private UserResponseDto convertToDto(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setFirstname(user.getFirstname());
        dto.setLastname(user.getLastname());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole().toString());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setBirthDate(user.getBirthDate());
        dto.setSalary(user.getSalary());
        dto.setTotalOrders(user.getTotalOrders());
        dto.setStationId(user.getStation() != null ? user.getStation().getId() : null);
        return dto;
    }

    public UserResponseDto findUserByEmail(String email) {
        return convertToDto(repository.findByEmail(email)
                .orElseThrow(() -> new NotFoundOrAlreadyExistException("User not found")));
    }

}
