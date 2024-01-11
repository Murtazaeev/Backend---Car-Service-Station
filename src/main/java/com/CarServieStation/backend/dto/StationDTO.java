package com.CarServieStation.backend.dto;

import com.CarServieStation.backend.entity.StationColorType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StationDTO {
    private Integer managerId;
    private String stationName;
    private StationColorType stationColorType;
    private List<Integer> employeeIds;

}