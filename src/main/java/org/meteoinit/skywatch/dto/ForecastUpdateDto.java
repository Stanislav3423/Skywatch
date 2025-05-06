package org.meteoinit.skywatch.dto;

import lombok.Data;

@Data
public class ForecastUpdateDto {
    private Long id;
    private String forecastType;
    private Double temperature;
    private Integer pressure;
    private Integer windDeg;
    private Double windSpeed;
    private Double precipitationAmount;
    private Double precipitationProbability;
}
