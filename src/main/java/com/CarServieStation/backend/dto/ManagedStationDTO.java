package com.CarServieStation.backend.dto;

import com.CarServieStation.backend.entity.Station;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


@Getter
@Setter
public class ManagedStationDTO {
    private Station station;
    private Integer managerId;
}