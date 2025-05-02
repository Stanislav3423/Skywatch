package org.meteoinit.skywatch.controller;

import lombok.RequiredArgsConstructor;
import org.meteoinit.skywatch.model.Location;
import org.meteoinit.skywatch.repository.LocationRepository;
import org.meteoinit.skywatch.service.ForecastService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
}