package com.CarServieStation.backend.controller;


import com.CarServieStation.backend.dto.StationDTO;
import com.CarServieStation.backend.entity.Station;
import com.CarServieStation.backend.service.StationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stations")
@RequiredArgsConstructor
public class StationController {

    private final StationService stationService;
    @PostMapping
    public ResponseEntity<Station> createStation(@RequestBody StationDTO stationDTO) {
        return ResponseEntity.ok(stationService.createStation(stationDTO));
    }

    @PutMapping("/{stationId}")
    public ResponseEntity<Station> updateStation(@PathVariable Integer stationId, @RequestBody StationDTO stationDTO) {
        return ResponseEntity.ok(stationService.updateStation(stationId, stationDTO));
    }

    @DeleteMapping("/{stationId}")
    public ResponseEntity<?> deleteStation(@PathVariable Integer stationId) {
        stationService.deleteStation(stationId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{stationId}")
    public ResponseEntity<Station> getStation(@PathVariable Integer stationId) {
        return ResponseEntity.ok(stationService.getStation(stationId));
    }

    @GetMapping
    public ResponseEntity<List<Station>> getAllStations() {
        return ResponseEntity.ok(stationService.getAllStations());
    }
}