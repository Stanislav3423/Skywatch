package org.meteoinit.skywatch.dto;

import lombok.Data;
import org.meteoinit.skywatch.model.Country;

@Data
public class TriggerDto {
    private Long id;
    private String name;
    private String condition;
    private String location;
    private String countryCode;

    public TriggerDto(Long id, String name, String condition, String location, String countryCode) {
        this.id = id;
        this.name = name;
        this.condition = condition;
        this.location = location;
        this.countryCode = countryCode;
    }
}
