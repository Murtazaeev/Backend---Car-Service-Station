package com.CarServieStation.backend.repository;

import com.CarServieStation.backend.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface StationRepository extends JpaRepository<Station, Integer> {
    @Query("SELECT s FROM Station s LEFT JOIN FETCH s.employees WHERE s.id = :stationId")
    Optional<Station> findByIdWithEmployees(Integer stationId);
}