package org.meteoinit.skywatch.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    /*@GetMapping
    public WeatherDto getWeather(@RequestParam String region) {
        return weatherService.getWeatherForRegion(region);
    }*/
}
