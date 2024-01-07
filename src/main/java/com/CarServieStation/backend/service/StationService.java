package com.CarServieStation.backend.service;



import com.CarServieStation.backend.dto.StationDTO;
import com.CarServieStation.backend.entity.Role;
import com.CarServieStation.backend.entity.Station;
import com.CarServieStation.backend.entity.User;
import com.CarServieStation.backend.repository.StationRepository;
import com.CarServieStation.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StationService {

    private final StationRepository stationRepository;
    private final UserRepository userRepository;


    public Station createStation(StationDTO stationDTO) {

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

        return stationRepository.save(station);
    }

    public Station updateStation(Integer stationId, StationDTO stationDTO) {
        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new RuntimeException("Station not found with id: " + stationId));

        // Update fields from DTO
        station.setStationName(stationDTO.getStationName());
        station.setStationColorType(stationDTO.getStationColorType());

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

        return stationRepository.save(station);
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

    public Station getStation(Integer stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(() -> new RuntimeException("Station not found with id: " + stationId));
    }

    public List<Station> getAllStations() {
        return stationRepository.findAll();
    }
}