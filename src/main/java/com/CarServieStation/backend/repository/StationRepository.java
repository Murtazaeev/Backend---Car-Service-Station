package com.CarServieStation.backend.repository;

import com.CarServieStation.backend.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StationRepository extends JpaRepository<Station, Integer> {
}