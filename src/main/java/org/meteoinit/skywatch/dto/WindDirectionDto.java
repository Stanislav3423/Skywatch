package org.meteoinit.skywatch.dto;

import lombok.Data;

@Data
public class WindDirectionDto {
    private Integer deg;
    private Double avgSpeed;
    public WindDirectionDto(Integer deg, Double avgSpeed) {
        this.deg = deg;
        this.avgSpeed = avgSpeed;
    }
}
