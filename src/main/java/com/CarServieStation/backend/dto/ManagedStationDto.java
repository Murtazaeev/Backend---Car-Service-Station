package com.CarServieStation.backend.dto;

import com.CarServieStation.backend.entity.Station;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ManagedStationDto {
    private Station station;
    private Integer managerId;
}