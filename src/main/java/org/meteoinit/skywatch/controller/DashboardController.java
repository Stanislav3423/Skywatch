package org.meteoinit.skywatch.controller;

import lombok.RequiredArgsConstructor;
import org.meteoinit.skywatch.dto.ForecastDto;
import org.meteoinit.skywatch.model.Location;
import org.meteoinit.skywatch.repository.LocationRepository;
import org.meteoinit.skywatch.service.ForecastService;
import org.springframework.beans.factory.annotation.Autowired;
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
}