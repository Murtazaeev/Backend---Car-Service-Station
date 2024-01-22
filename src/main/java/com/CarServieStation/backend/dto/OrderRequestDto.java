package com.CarServieStation.backend.dto;

import com.CarServieStation.backend.entity.OrderState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrderRequestDto {
    private Integer clientId;
    private Integer stationId;
    private String serviceType;
    private OrderState state;
    private Double cost;
    private String licenceNumber;
}