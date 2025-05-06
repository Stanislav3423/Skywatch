package org.meteoinit.skywatch.dto;

import lombok.Data;

@Data
public class ForecastDto {
    private Long id;
    private String dayOrHour;
    private String date;
    private String icon;
    private Float temperature;
    private Integer pressure;
    private Integer humidity;
    private Integer wind_dir;
    private Float wind_speed;
    private Float precipitation_amount;
    private Float precipitation_probability;
    private String condition;


}