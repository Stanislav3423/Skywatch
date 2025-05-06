package org.meteoinit.skywatch.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HumidityDto {
    private Integer year;
    private Integer month;
    private Integer day;
    private Integer hour;
    private Double avgHum;

    public LocalDateTime getTimestamp() {
        return LocalDateTime.of(year, month, day, hour != null ? hour : 0, 0);
    }

    public HumidityDto(Integer year, Integer month, Integer day, Integer hour, Double avgHum) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.avgHum = avgHum;
    }

    public HumidityDto(Integer year, Integer month, Integer day, Double avgHum) {
        this(year, month, day, 0, avgHum);
    }

    public HumidityDto(Integer year, Integer month, Double avgHum) {
        this.year = year;
        this.month = month;
        this.day = 1;
        this.hour = 0;
        this.avgHum = avgHum;
    }
}
