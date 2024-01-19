package com.CarServieStation.backend.repository;

import com.CarServieStation.backend.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, Integer> {
}
