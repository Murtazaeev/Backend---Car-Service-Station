package com.CarServieStation.backend.dto;

import com.CarServieStation.backend.entity.ColorType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CarRequestDto {
    private String model;
    private String make;
    private String licenceNumber;
    private ColorType colorType;
}