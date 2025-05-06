package org.meteoinit.skywatch.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReportDto {
    private Long id;
    private String name;
    private String location;
    private String username;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Double avgTemp;
    private Double avgHumidity;
    private Double maxTemp;
    private Double minTemp;
    private Double avgCloudiness;
    private Double avgPressure;
    private Double avgWindSpeed;
    private LocalDateTime createdAt;
}
