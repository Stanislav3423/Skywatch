package org.meteoinit.skywatch.dto;

import lombok.Data;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

@Data
public class ReportRequest {
    private String name;
    private String location;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Double avgTemp;
    private Double avgHumidity;
    private Double maxTemp;
    private Double minTemp;
    private Double avgCloudiness;
    private Double avgPressure;
    private Double avgWindSpeed;
}
