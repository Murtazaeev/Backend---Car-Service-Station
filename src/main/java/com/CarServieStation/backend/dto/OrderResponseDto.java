package com.CarServieStation.backend.dto;

import com.CarServieStation.backend.entity.OrderState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrderResponseDto {
    private Integer id;
    private String registerNumber;
    private Date savedDate;
    private String serviceType;
    private Double cost;
    private String licenceNumber;
    private OrderState state;
    private OrderManagerResponseDto manager;
    private OrderClientResponseDto client;
    private OrderStationResponseDto station;
}