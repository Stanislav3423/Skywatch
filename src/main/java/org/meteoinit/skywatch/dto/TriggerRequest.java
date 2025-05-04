package org.meteoinit.skywatch.dto;

import lombok.Data;

@Data
public class TriggerRequest {
    private String name;
    private String parameter; // 'temperature', 'humidity', 'precipitation'
    private String operator; // '>', '<', '='
    private double value;
    private Long locationId;
    private String username;
}