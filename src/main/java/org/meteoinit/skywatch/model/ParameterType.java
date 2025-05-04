package org.meteoinit.skywatch.model;

public enum ParameterType {
    TEMPERATURE,
    HUMIDITY,
    PRECIPITATION;
    public static ParameterType fromString(String value) {
        return ParameterType.valueOf(value.toUpperCase());
    }
}
