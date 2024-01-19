package com.CarServieStation.backend.service;


import com.CarServieStation.backend.dto.ManagedStationDto;
import com.CarServieStation.backend.dto.StationRequestDto;
import com.CarServieStation.backend.dto.StationResponseDto;
import com.CarServieStation.backend.entity.Employee;
import com.CarServieStation.backend.entity.Role;
import com.CarServieStation.backend.entity.Station;
import com.CarServieStation.backend.entity.User;
import com.CarServieStation.backend.exception.NotFoundOrAlreadyExistException;
import com.CarServieStation.backend.repository.EmployeeRepository;
import com.CarServieStation.backend.repository.StationRepository;
import com.CarServieStation.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StationService {

    private final StationRepository stationRepository;
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;



    @Transactional
    public StationResponseDto createStation(StationRequestDto stationRequestDTO) {

        Station station = new Station();
        station.setStationName(stationRequestDTO.getStationName());
        station.setStationColorType(stationRequestDTO.getStationColorType());

        if (stationRequestDTO.getManagerId() != null) {
            User manager = userRepository.findById(stationRequestDTO.getManagerId())
                    .orElseThrow(() -> new NotFoundOrAlreadyExistException("Manager not found with id: " + stationRequestDTO.getManagerId()));

            if (manager.getStation() != null) {
                throw new IllegalStateException("Manager is already assigned to a station");
            }

            // Check if the manager's role is "MANAGER"
            if (!manager.getRole().equals(Role.MANAGER)) {
                throw new IllegalStateException("User does not have the required 'MANAGER' role");
            }

            station.setUser(manager);
        }

        if (stationRequestDTO.getEmployeeIds() != null && !stationRequestDTO.getEmployeeIds().isEmpty()) {
            for (Integer employeeId : stationRequestDTO.getEmployeeIds()) {
                Employee employee = employeeRepository.findById(employeeId)
                        .orElseThrow(() -> new NotFoundOrAlreadyExistException("Employee not found with id: " + employeeId));

                if (employee.getStation() != null) {
                    throw new IllegalStateException("One of the employees is already assigned to a station");
                }
                station.getEmployees().add(employee);
                employee.setStation(station);
            }
        }
        Station savedStation = stationRepository.save(station);

        return mapToStationDTO(savedStation);
    }


    @Transactional
    public StationResponseDto updateStation(Integer stationId, StationRequestDto stationRequestDTO) {
        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new NotFoundOrAlreadyExistException("Station not found with id: " + stationId));

        // Update station fields
        station.setStationName(stationRequestDTO.getStationName());
        station.setStationColorType(stationRequestDTO.getStationColorType());

        // handle manager reassignment
        if (stationRequestDTO.getManagerId() != null) {
            User newManager = userRepository.findById(stationRequestDTO.getManagerId())
                    .orElseThrow(() -> new NotFoundOrAlreadyExistException("User not found with id: " + stationRequestDTO.getManagerId()));

            // Check manager role
            if (!newManager.getRole().equals(Role.MANAGER)) {
                throw new IllegalStateException("User does not have the required 'MANAGER' role");
            }

            // Disassociate old manager
            if (station.getUser() != null && !station.getUser().getId().equals(stationRequestDTO.getManagerId())) {
                station.getUser().setStation(null);
            }

            station.setUser(newManager);
        } else {
            // Handle unassigning a manager
            if (station.getUser() != null) {
                station.getUser().setStation(null);
            }
            station.setUser(null);
        }

        // Remove all current employees from the station
        for (Employee employee : station.getEmployees()) {
            employee.setStation(null);
        }
        station.getEmployees().clear();

        // Re-associate employees from the request
        for (Integer employeeId : stationRequestDTO.getEmployeeIds()) {
            Employee employee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new NotFoundOrAlreadyExistException("Employee not found with id: " + employeeId));

            station.getEmployees().add(employee);
            employee.setStation(station);
        }

        Station updatedStation = stationRepository.save(station);
        return mapToStationDTO(updatedStation);
    }


    @Transactional
    public void deleteStation(Integer stationId) {
        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new NotFoundOrAlreadyExistException("Station not found with id: " + stationId));
        // Disconnect the manager if assigned
        if (station.getUser() != null) {
            User user = station.getUser();
            user.setStation(null);
            userRepository.save(user);
        }
        // Disconnect all employees assigned to the station
        if (station.getEmployees() != null && !station.getEmployees().isEmpty()) {
            for (Employee employee : station.getEmployees()) {
                employee.setStation(null);
                employeeRepository.save(employee);
            }
        }

        stationRepository.deleteById(stationId);
    }

    public StationResponseDto getStation(Integer stationId) {
        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new NotFoundOrAlreadyExistException("Station not found with id: " + stationId));
        return mapToStationDTO(station);
    }

    public List<StationResponseDto> getAllStations() {
        List<Station> stations = stationRepository.findAll();
        return stations.stream()
                .map(this::mapToStationDTO)
                .collect(Collectors.toList());
    }


    public Optional<ManagedStationDto> getStationManagedByUserWithEmployees(Principal principal) {
        User manager = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new NotFoundOrAlreadyExistException("User not found"));

        if (manager.getRole() != Role.MANAGER) {
            throw new IllegalStateException("User is not authorized as a manager");
        }

        if (manager.getStation() != null) {
            Station station = stationRepository.findByIdWithEmployees(manager.getStation().getId())
                    .orElseThrow(() -> new NotFoundOrAlreadyExistException("Station not found"));
            ManagedStationDto managedStationDTO = new ManagedStationDto();
            managedStationDTO.setStation(station);
            managedStationDTO.setManagerId(manager.getId());
            return Optional.of(managedStationDTO);
        }

        return Optional.empty();
    }

    private StationResponseDto mapToStationDTO(Station station) {
        StationResponseDto dto = new StationResponseDto();
        dto.setId(station.getId());
        dto.setManagerId(station.getUser() != null ? station.getUser().getId() : null);
        dto.setStationName(station.getStationName());
        dto.setStationColorType(station.getStationColorType());
        dto.setEmployees(new ArrayList<>(station.getEmployees())); // Directly set the list of employees
        return dto;
    }
}