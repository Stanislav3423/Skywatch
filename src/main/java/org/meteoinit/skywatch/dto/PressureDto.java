package org.meteoinit.skywatch.dto;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class PressureDto {
    private Integer year;
    private Integer month;
    private Integer day;
    private Integer hour;
    private Double avgPressure;

    public LocalDateTime getTimestamp() {
        return LocalDateTime.of(year, month, day, hour != null ? hour : 0, 0);
    }

    public PressureDto(Integer year, Integer month, Integer day, Integer hour, Double avgPressure) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.avgPressure = avgPressure;
    }

    public PressureDto(Integer year, Integer month, Integer day, Double avgPressure) { // для daily
        this(year, month, day, 0, avgPressure);
    }

    public PressureDto(Integer year, Integer month, Double avgPressure) { // для monthly
        this.year = year;
        this.month = month;
        this.day = 1;
        this.hour = 0;
        this.avgPressure = avgPressure;
    }
}