package com.CarServieStation.backend.repository;

import com.CarServieStation.backend.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CarRepository extends JpaRepository<Car, Integer> {

    @Query("SELECT c FROM Car c WHERE c.client IS NULL")
    List<Car> findUnassignedCars();

    Optional<Car> findByLicenceNumber(String licenceNumber);
}
