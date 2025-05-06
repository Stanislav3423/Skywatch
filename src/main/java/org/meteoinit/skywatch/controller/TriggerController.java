package org.meteoinit.skywatch.controller;
import lombok.RequiredArgsConstructor;
import org.meteoinit.skywatch.dto.ForecastDto;
import org.meteoinit.skywatch.dto.LocationDto;
import org.meteoinit.skywatch.dto.TriggerDto;
import org.meteoinit.skywatch.dto.TriggerRequest;
import org.meteoinit.skywatch.model.DailyForecast;
import org.meteoinit.skywatch.model.HourlyForecast;
import org.meteoinit.skywatch.model.Location;
import org.meteoinit.skywatch.model.Trigger;
import org.meteoinit.skywatch.repository.LocationRepository;
import org.meteoinit.skywatch.service.ForecastService;
import org.meteoinit.skywatch.service.TriggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/triggers")
@RequiredArgsConstructor
public class TriggerController {

    private final TriggerService triggerService;
    private final ForecastService forecastService;

    @GetMapping("/{triggerId}/daily-forecasts")
    public ResponseEntity<List<ForecastDto>> getMatchingDailyForecasts(@PathVariable Long triggerId) {
        List<DailyForecast> forecasts = triggerService.getMatchingDailyForecasts(triggerId);
        System.out.println(forecasts.size());
        List<ForecastDto> dtos = forecasts.stream()
                .map(forecastService::mapDailyForecastToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{triggerId}/hourly-forecasts")
    public ResponseEntity<List<ForecastDto>> getMatchingHourlyForecasts(@PathVariable Long triggerId) {
        List<HourlyForecast> forecasts = triggerService.getMatchingHourlyForecasts(triggerId);
        List<ForecastDto> dtos = forecasts.stream()
                .map(forecastService::mapHourlyForecastToDto)  // Треба окрема функція для hourly
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createTrigger(@RequestBody TriggerRequest request) {
        Trigger trigger = triggerService.createTrigger(request);
        //return ResponseEntity.status(HttpStatus.CREATED).body(trigger);
        return ResponseEntity.ok("Trigger created successfully");
    }

    @DeleteMapping("/{triggerId}")
    public ResponseEntity<?> deleteTrigger(@PathVariable Long triggerId, @RequestParam String username) {
        triggerService.deleteTrigger(triggerId, username);
        return ResponseEntity.ok("Trigger deleted successfully");
    }

    @GetMapping("/all-for-user/{username}")
    public ResponseEntity<List<TriggerDto>> getAllTriggersForUser(@PathVariable String username) {
        List<Trigger> triggers = triggerService.searchTriggersByName(username);

        List<TriggerDto> TriggerDtos = triggers.stream()
                .map(trigger -> new TriggerDto(
                        trigger.getId(),
                        trigger.getName(),
                        trigger.getParameter().toString().toLowerCase() + " " + trigger.getOperator().getSymbol() + " " + trigger.getValue(),
                        trigger.getLocation().getName(),
                        trigger.getLocation().getCountry().getCode()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(TriggerDtos);
    }
}