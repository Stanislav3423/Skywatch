package org.meteoinit.skywatch.controller;

import lombok.RequiredArgsConstructor;
import org.meteoinit.skywatch.dto.HumidityDto;
import org.meteoinit.skywatch.dto.PressureDto;
import org.meteoinit.skywatch.dto.TemperatureDto;
import org.meteoinit.skywatch.dto.WindDirectionDto;
import org.meteoinit.skywatch.service.AnalyticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/temperature")
    public List<TemperatureDto> getTemperature(@RequestParam(required = false) Long locationId,
                                               @RequestParam String startDate,
                                               @RequestParam String endDate,
                                               @RequestParam String period) {
        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);
        return analyticsService.getTemperatureData(locationId, start, end, period);
    }

    @GetMapping("/wind")
    public List<WindDirectionDto> getWind(@RequestParam(required = false) Long locationId,
                                          @RequestParam String startDate,
                                          @RequestParam String endDate) {
        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);
        return analyticsService.getWindAnalysis(locationId, start, end);
    }

    @GetMapping("/pressure")
    public List<PressureDto> getPressure(@RequestParam(required = false) Long locationId,
                                         @RequestParam String startDate,
                                         @RequestParam String endDate,
                                         @RequestParam String period) {
        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);
        return analyticsService.getPressureData(locationId, start, end, period);
    }

    @GetMapping("/humidity")
    public List<HumidityDto> getHumidity(@RequestParam(required = false) Long locationId,
                                         @RequestParam String startDate,
                                         @RequestParam String endDate,
                                         @RequestParam String period) {
        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);
        return analyticsService.getHumidityData(locationId, start, end, period);
    }

    @GetMapping("/summary")
    public Map<String, Double> getSummary(@RequestParam(required = false) Long locationId,
                                          @RequestParam String startDate,
                                          @RequestParam String endDate) {
        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);
        Map<String, Double> result = new HashMap<>();
        result.put("avgTemp", analyticsService.getAverageTemperature(locationId, start, end));
        result.put("avgHumidity", analyticsService.getAverageHumidity(locationId, start, end));
        result.put("avgPressure", analyticsService.getAveragePressure(locationId, start, end));
        result.put("avgWindSpeed", analyticsService.getAverageWindSpeed(locationId, start, end));
        result.put("maxTemp", analyticsService.getMaxTemperature(locationId, start, end));
        result.put("minTemp", analyticsService.getMinTemperature(locationId, start, end));
        result.put("avgCloudiness", analyticsService.getAverageCloudiness(locationId, start, end));

        return result;
    }
}