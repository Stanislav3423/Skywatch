package org.meteoinit.skywatch.dto;

import lombok.Data;
@Data
public class LocationDto {
    private Long id;
    private String name;
    private Double lon;
    private Double lat;
    private String countryCode;
    private Long id_json;

    public LocationDto(Long id, String name, Double lon, Double lat, String countryCode, Long id_json) {
        this.id = id;
        this.name = name;
        this.lon = lon;
        this.lat = lat;
        this.countryCode = countryCode;
        this.id_json = id_json;
    }
}
