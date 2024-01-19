package com.CarServieStation.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserResponseDto {
    private Integer id;
    private String firstname;
    private String lastname;
    private String email;
    private String role;
    private String phoneNumber;
    private Date birthDate;
    private double salary;
    private int totalOrders;
    private Integer stationId;
}
