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
public class EmployeePatchDto {
    private String firstname;
    private String lastname;
    private Date birthDate;
    private String email;
    private String phoneNumber;
    private double salary;
}
