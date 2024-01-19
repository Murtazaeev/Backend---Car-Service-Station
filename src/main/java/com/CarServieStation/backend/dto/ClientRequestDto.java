package com.CarServieStation.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ClientRequestDto {
    private String firstname;
    private String lastname;
    private String email;
    private String phoneNumber;
    private List<CarRequestDto> cars;
}