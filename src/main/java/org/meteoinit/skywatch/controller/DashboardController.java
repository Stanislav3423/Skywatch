package org.meteoinit.skywatch.controller;

import lombok.RequiredArgsConstructor;
import org.meteoinit.skywatch.dto.ForecastDto;
import org.meteoinit.skywatch.dto.ForecastUpdateDto;
import org.meteoinit.skywatch.model.Location;
import org.meteoinit.skywatch.repository.LocationRepository;
import org.meteoinit.skywatch.service.ForecastService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dashboard")
public class DashboardController {
    @Autowired
    private ForecastService forecastService;

    @GetMapping("/{locationId}")
    public String refreshForecast(@PathVariable Long locationId) throws Exception {
        forecastService.refreshForecasts(locationId);
        return "Forecast updated for location " + locationId;
    }
    @GetMapping("/forecast/7day/{locationId}")
    public List<ForecastDto> get7DayForecast(@PathVariable Long locationId) throws Exception {
        forecastService.refreshForecasts(locationId);
        return forecastService.get7DayForecast(locationId);
    }

    @GetMapping("/forecast/12hour/{locationId}")
    public List<ForecastDto> get12HourForecast(@PathVariable Long locationId) throws Exception {
        forecastService.refreshForecasts(locationId);
        return forecastService.get12HourForecast(locationId);
    }

    @PutMapping("/forecast/update")
    public ResponseEntity<?> updateForecast(@RequestBody ForecastUpdateDto updateDto) {
        try {
            if ("7day".equals(updateDto.getForecastType())) {
                forecastService.updateDailyForecast(updateDto);
            } else if ("12hour".equals(updateDto.getForecastType())) {
                forecastService.updateHourlyForecast(updateDto);
            } else {
                return ResponseEntity.badRequest().body("Invalid forecast type");
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating forecast: " + e.getMessage());
        }
    }

    @GetMapping("/forecast/7day/standart/{locationId}")
    public List<ForecastDto> get7DayStandartForecast(@PathVariable Long locationId) throws Exception {
        return forecastService.get7DayForecast(locationId);
    }

    @GetMapping("/forecast/12hour/standart/{locationId}")
    public List<ForecastDto> get12HourStandartForecast(@PathVariable Long locationId) throws Exception {
        return forecastService.get12HourForecast(locationId);
    }
}