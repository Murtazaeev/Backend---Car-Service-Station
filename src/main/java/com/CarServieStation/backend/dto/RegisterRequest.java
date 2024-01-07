package com.CarServieStation.backend.dto;

import com.CarServieStation.backend.entity.Role;

import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class RegisterRequest {

    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private Role role;
    private String phoneNumber;
    private Date birthDate;
    private double salary;
    private int totalOrders;
}
