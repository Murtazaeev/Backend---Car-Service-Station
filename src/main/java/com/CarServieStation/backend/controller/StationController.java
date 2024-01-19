package com.CarServieStation.backend.controller;


import com.CarServieStation.backend.dto.ManagedStationDto;
import com.CarServieStation.backend.dto.StationRequestDto;
import com.CarServieStation.backend.dto.StationResponseDto;
import com.CarServieStation.backend.service.StationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/stations")
@RequiredArgsConstructor
public class StationController {

    private final StationService stationService;
    @PostMapping
    public ResponseEntity<StationResponseDto> createStation(@RequestBody StationRequestDto stationRequestDTO) {
        return ResponseEntity.ok(stationService.createStation(stationRequestDTO));
    }

    @PutMapping("/{stationId}")
    public ResponseEntity<StationResponseDto> updateStation(@PathVariable Integer stationId, @RequestBody StationRequestDto stationRequestDTO) {
        return ResponseEntity.ok(stationService.updateStation(stationId, stationRequestDTO));
    }

    @DeleteMapping("/{stationId}")
    public ResponseEntity<?> deleteStation(@PathVariable Integer stationId) {
        stationService.deleteStation(stationId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{stationId}")
    public ResponseEntity<StationResponseDto> getStation(@PathVariable Integer stationId) {
        return ResponseEntity.ok(stationService.getStation(stationId));
    }

    @GetMapping
    public ResponseEntity<List<StationResponseDto>> getAllStations() {
        return ResponseEntity.ok(stationService.getAllStations());
    }

    @GetMapping("/employees")
    public ResponseEntity<?> getManagedStationWithEmployees(Principal principal) {
        Optional<ManagedStationDto> managedStationDTO = stationService.getStationManagedByUserWithEmployees(principal);

        if (managedStationDTO.isPresent()) {
            return ResponseEntity.ok(managedStationDTO.get());
        } else {
            // Returning an empty array when no station is found
            return ResponseEntity.ok(new ArrayList<>());
        }
    }
}