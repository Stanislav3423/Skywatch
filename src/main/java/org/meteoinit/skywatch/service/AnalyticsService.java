package org.meteoinit.skywatch.service;

import lombok.RequiredArgsConstructor;
import org.meteoinit.skywatch.dto.HumidityDto;
import org.meteoinit.skywatch.dto.PressureDto;
import org.meteoinit.skywatch.dto.TemperatureDto;
import org.meteoinit.skywatch.dto.WindDirectionDto;
import org.meteoinit.skywatch.repository.ObservationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyticsService {
    private final ObservationRepository observationRepository;

    public List<TemperatureDto> getTemperatureData(Long locationId, LocalDateTime start, LocalDateTime end, String period) {
        return switch (period) {
            case "hour" -> observationRepository.getHourlyTemperature(locationId, start, end);
            case "day" -> observationRepository.getDailyTemperature(locationId, start, end);
            case "month" -> observationRepository.getMonthlyTemperature(locationId, start, end);
            default -> throw new IllegalArgumentException("Invalid period: " + period);
        };
    }

    public List<WindDirectionDto> getWindAnalysis(Long locationId, LocalDateTime start, LocalDateTime end) {
        return observationRepository.getWindAnalysis(locationId, start, end);
    }

    public List<PressureDto> getPressureData(Long locationId, LocalDateTime start, LocalDateTime end, String period) {
        return switch (period) {
            case "hour" -> observationRepository.getHourlyPressure(locationId, start, end);
            case "day" -> observationRepository.getDailyPressure(locationId, start, end);
            case "month" -> observationRepository.getMonthlyPressure(locationId, start, end);
            default -> throw new IllegalArgumentException("Invalid period: " + period);
        };
    }

    public List<HumidityDto> getHumidityData(Long locationId, LocalDateTime start, LocalDateTime end, String period) {
        return switch (period) {
            case "hour" -> observationRepository.getHourlyHumidity(locationId, start, end);
            case "day" -> observationRepository.getDailyHumidity(locationId, start, end);
            case "month" -> observationRepository.getMonthlyHumidity(locationId, start, end);
            default -> throw new IllegalArgumentException("Invalid period: " + period);
        };
    }

    public Double getAverageTemperature(Long locationId, LocalDateTime start, LocalDateTime end) {
        return observationRepository.getAverageTemperature(locationId, start, end);
    }

    public Double getAverageHumidity(Long locationId, LocalDateTime start, LocalDateTime end) {
        return observationRepository.getAverageHumidity(locationId, start, end);
    }

    public Double getAveragePressure(Long locationId, LocalDateTime start, LocalDateTime end) {
        return observationRepository.getAveragePressure(locationId, start, end);
    }

    public Double getAverageWindSpeed(Long locationId, LocalDateTime start, LocalDateTime end) {
        return observationRepository.getAverageWindSpeed(locationId, start, end);
    }

    public Double getMaxTemperature(Long locationId, LocalDateTime start, LocalDateTime end) {
        return observationRepository.getMaxTemperature(locationId, start, end);
    }

    public Double getMinTemperature(Long locationId, LocalDateTime start, LocalDateTime end) {
        return observationRepository.getMinTemperature(locationId, start, end);
    }

    public Double getAverageCloudiness(Long locationId, LocalDateTime start, LocalDateTime end) {
        return observationRepository.getAverageCloudiness(locationId, start, end);
    }
}
