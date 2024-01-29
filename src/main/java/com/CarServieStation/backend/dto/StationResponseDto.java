package com.CarServieStation.backend.dto;
import com.CarServieStation.backend.entity.ColorType;
import com.CarServieStation.backend.entity.Employee;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StationResponseDto {

    private Integer id;
    private StationManagerResponse manager;
    private String stationName;
    private ColorType colorType;
    private List<Employee> employees;
}