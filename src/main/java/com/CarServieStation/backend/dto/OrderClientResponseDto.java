package com.CarServieStation.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrderClientResponseDto {
    private Integer id;
    private String firstname;
    private String lastname;
    private String email;
    private String phoneNumber;
}