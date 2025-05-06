package org.meteoinit.skywatch.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TemperatureDto {
    private Integer year;
    private Integer month;
    private Integer day;
    private Integer hour;
    private Double avgTemp;

    public LocalDateTime getTimestamp() {
        return LocalDateTime.of(year, month, day, hour != null ? hour : 0, 0);
    }

    public TemperatureDto(Integer year, Integer month, Integer day, Integer hour, Double avgTemp) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.avgTemp = avgTemp;
    }

    public TemperatureDto(Integer year, Integer month, Integer day, Double avgTemp) { // для daily
        this(year, month, day, 0, avgTemp);
    }

    public TemperatureDto(Integer year, Integer month, Double avgTemp) { // для monthly
        this.year = year;
        this.month = month;
        this.day = 1;
        this.hour = 0;
        this.avgTemp = avgTemp;
    }
}
