package com.CarServieStation.backend.service;



import com.CarServieStation.backend.dto.Employee;
import com.CarServieStation.backend.dto.ManagedStationDTO;
import com.CarServieStation.backend.dto.StationDTO;
import com.CarServieStation.backend.entity.Role;
import com.CarServieStation.backend.entity.Station;
import com.CarServieStation.backend.entity.User;
import com.CarServieStation.backend.repository.EmployeeRepository;
import com.CarServieStation.backend.repository.StationRepository;
import com.CarServieStation.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StationService {

    private final StationRepository stationRepository;
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;


    public StationDTO createStation(StationDTO stationDTO) {

        Station station = new Station();
        station.setStationName(stationDTO.getStationName());
        station.setStationColorType(stationDTO.getStationColorType());

        if (stationDTO.getManagerId() != null) {
            User manager = userRepository.findById(stationDTO.getManagerId())
                    .orElseThrow(() -> new RuntimeException("Manager not found with id: " + stationDTO.getManagerId()));

            if (manager.getStation() != null) {
                throw new IllegalStateException("Manager is already assigned to a station");
            }

            // Check if the manager's role is "MANAGER"
            if (!manager.getRole().equals(Role.MANAGER)) {
                throw new IllegalStateException("User does not have the required 'MANAGER' role");
            }

            station.setUser(manager);
        }

        if (stationDTO.getEmployeeIds() != null && !stationDTO.getEmployeeIds().isEmpty()) {
            for (Integer employeeId : stationDTO.getEmployeeIds()) {
                Employee employee = employeeRepository.findById(employeeId)
                        .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));

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

    public StationDTO updateStation(Integer stationId, StationDTO stationDTO) {
        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new RuntimeException("Station not found with id: " + stationId));

        // Update fields from DTO
        station.setStationName(stationDTO.getStationName());
        station.setStationColorType(stationDTO.getStationColorType());

        // Assigning a manager to the station
        if (stationDTO.getManagerId() != null) {
            User newManager = userRepository.findById(stationDTO.getManagerId())
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + stationDTO.getManagerId()));

            // Check if the manager's role is "MANAGER"
            if (!newManager.getRole().equals(Role.MANAGER)) {
                throw new IllegalStateException("User does not have the required 'MANAGER' role");
            }

            if (newManager.getStation() != null && !newManager.getStation().getId().equals(stationId)) {
                throw new IllegalStateException("Manager is already assigned to a station");
            }

            station.setUser(newManager);
        } else {
            station.setUser(null);  // To handle unassigning a user from a station
        }

        // Assigning employees to the station
        if (stationDTO.getEmployeeIds() != null && !stationDTO.getEmployeeIds().isEmpty()) {
            for (Integer employeeId : stationDTO.getEmployeeIds()) {
                Employee employee = employeeRepository.findById(employeeId)
                        .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));

                if (employee.getStation() != null && !employee.getStation().getId().equals(stationId)) {
                    throw new IllegalStateException("One of the employees is already assigned to another station");
                }
                station.getEmployees().add(employee);
                employee.setStation(station);
            }
        }

        Station updatedStation = stationRepository.save(station);
        return mapToStationDTO(updatedStation);
    }

    public void deleteStation(Integer stationId) {
        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new RuntimeException("Station not found with id: " + stationId));

        if (station.getUser() != null) {
            User user = station.getUser();
            user.setStation(null);
            userRepository.save(user);
        }

        stationRepository.deleteById(stationId);
    }

    public StationDTO getStation(Integer stationId) {
        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new RuntimeException("Station not found with id: " + stationId));
        return mapToStationDTO(station);
    }

    public List<StationDTO> getAllStations() {
        List<Station> stations = stationRepository.findAll();
        return stations.stream()
                .map(this::mapToStationDTO)
                .collect(Collectors.toList());
    }


    public Optional<ManagedStationDTO> getStationManagedByUserWithEmployees(Principal principal) {
        User manager = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (manager.getRole() != Role.MANAGER) {
            throw new IllegalStateException("User is not authorized as a manager");
        }

        if (manager.getStation() != null) {
            Station station = stationRepository.findByIdWithEmployees(manager.getStation().getId())
                    .orElseThrow(() -> new RuntimeException("Station not found"));
            ManagedStationDTO managedStationDTO = new ManagedStationDTO();
            managedStationDTO.setStation(station);
            managedStationDTO.setManagerId(manager.getId());
            return Optional.of(managedStationDTO);
        }

        return Optional.empty();
    }

    private StationDTO mapToStationDTO(Station station) {
        StationDTO dto = new StationDTO();
        dto.setManagerId(station.getUser() != null ? station.getUser().getId() : null);
        dto.setStationName(station.getStationName());
        dto.setStationColorType(station.getStationColorType());
        dto.setEmployeeIds(station.getEmployees().stream().map(Employee::getId).collect(Collectors.toList()));
        return dto;
    }
}