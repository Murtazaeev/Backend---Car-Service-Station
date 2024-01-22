package com.CarServieStation.backend.controller;


import com.CarServieStation.backend.dto.CarResponseDto;
import com.CarServieStation.backend.entity.Car;
import com.CarServieStation.backend.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cars")
public class CarController {

    @Autowired
    private CarService carService;

    @PostMapping
    public ResponseEntity<CarResponseDto> createCar(@RequestBody Car car) {
        CarResponseDto newCar = carService.createCar(car);
        return ResponseEntity.ok(newCar);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarResponseDto> getCar(@PathVariable Integer id) {
        CarResponseDto car = carService.getCar(id);
        return ResponseEntity.ok(car);
    }

    @GetMapping
    public ResponseEntity<List<CarResponseDto>> getAllCars() {
        List<CarResponseDto> cars = carService.getAllCars();
        return ResponseEntity.ok(cars);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarResponseDto> updateCar(@PathVariable Integer id, @RequestBody Car carDetails) {
        CarResponseDto updatedCar = carService.updateCar(id, carDetails);
        return ResponseEntity.ok(updatedCar);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCar(@PathVariable Integer id) {
        carService.deleteCar(id);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/unassigned")
    public ResponseEntity<List<CarResponseDto>> getUnassignedCars() {
        List<CarResponseDto> unassignedCars = carService.getUnassignedCars();
        return ResponseEntity.ok(unassignedCars);
    }
}