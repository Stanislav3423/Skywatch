package org.meteoinit.skywatch.service;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.meteoinit.skywatch.dto.TriggerRequest;
import org.meteoinit.skywatch.model.*;
import org.meteoinit.skywatch.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TriggerService {

    private final TriggerRepository triggerRepository;
    private final DailyForecastRepository dailyForecastRepository;
    private final HourlyForecastRepository hourlyForecastRepository;

    private final UserRepository userRepository;
    private final LocationRepository locationRepository;

    /*public List<DailyForecast> getMatchingDailyForecasts(Long triggerId) {
        Trigger trigger = triggerRepository.findById(triggerId)
                .orElseThrow(() -> new RuntimeException("Trigger not found"));

        String parameter = String.valueOf(trigger.getParameter()).toLowerCase();
        String operator = trigger.getOperator().getSymbol();
        System.out.println(operator);
        System.out.println(parameter);
        double value = trigger.getValue();
        Long locationId = trigger.getLocation().getId();

        return dailyForecastRepository.findAllByTriggerCondition(parameter, operator, value, locationId);
    }*/

    public List<DailyForecast> getMatchingDailyForecasts(Long triggerId) {
        Trigger trigger = triggerRepository.findById(triggerId)
                .orElseThrow(() -> new RuntimeException("Trigger not found"));

        String parameter = String.valueOf(trigger.getParameter()).toLowerCase();
        String operator = trigger.getOperator().getSymbol();
        System.out.println(parameter);
        double value = trigger.getValue();
        Long locationId = trigger.getLocation().getId();

        if (parameter.equals("temperature")) {
            switch (operator) {
                case ">":
                    return dailyForecastRepository.findTemperatureGreater(locationId, value);
                case "<":
                    return dailyForecastRepository.findTemperatureLess(locationId, value);
                case "=":
                    return dailyForecastRepository.findTemperatureEqual(locationId, value);
                default:
                    throw new IllegalArgumentException("Unsupported operator: " + operator);
            }
        } else if (parameter.equals("humidity")) {
            switch (operator) {
                case ">":
                    return dailyForecastRepository.findHumidityGreater(locationId, value);
                case "<":
                    return dailyForecastRepository.findHumidityLess(locationId, value);
                case "=":
                    return dailyForecastRepository.findHumidityEqual(locationId, value);
                default:
                    throw new IllegalArgumentException("Unsupported operator: " + operator);
            }
        } else if (parameter.equals("precipitation")) {
            switch (operator) {
                case ">":
                    return dailyForecastRepository.findPrecipitationGreater(locationId, value);
                case "<":
                    return dailyForecastRepository.findPrecipitationLess(locationId, value);
                case "=":
                    return dailyForecastRepository.findPrecipitationEqual(locationId, value);
                default:
                    throw new IllegalArgumentException("Unsupported operator: " + operator);
            }
        } else {
            throw new IllegalArgumentException("Unsupported parameter: " + parameter);
        }
    }

    public List<HourlyForecast> getMatchingHourlyForecasts(Long triggerId) {
        Trigger trigger = triggerRepository.findById(triggerId)
                .orElseThrow(() -> new RuntimeException("Trigger not found"));

        String parameter = String.valueOf(trigger.getParameter()).toLowerCase();
        String operator = trigger.getOperator().getSymbol();
        System.out.println(parameter);
        double value = trigger.getValue();
        Long locationId = trigger.getLocation().getId();

        if (parameter.equals("temperature")) {
            switch (operator) {
                case ">":
                    return hourlyForecastRepository.findTemperatureGreater(locationId, value);
                case "<":
                    return hourlyForecastRepository.findTemperatureLess(locationId, value);
                case "=":
                    return hourlyForecastRepository.findTemperatureEqual(locationId, value);
                default:
                    throw new IllegalArgumentException("Unsupported operator: " + operator);
            }
        } else if (parameter.equals("humidity")) {
            switch (operator) {
                case ">":
                    return hourlyForecastRepository.findHumidityGreater(locationId, value);
                case "<":
                    return hourlyForecastRepository.findHumidityLess(locationId, value);
                case "=":
                    return hourlyForecastRepository.findHumidityEqual(locationId, value);
                default:
                    throw new IllegalArgumentException("Unsupported operator: " + operator);
            }
        } else if (parameter.equals("precipitation")) {
            switch (operator) {
                case ">":
                    return hourlyForecastRepository.findPrecipitationGreater(locationId, value);
                case "<":
                    return hourlyForecastRepository.findPrecipitationLess(locationId, value);
                case "=":
                    return hourlyForecastRepository.findPrecipitationEqual(locationId, value);
                default:
                    throw new IllegalArgumentException("Unsupported operator: " + operator);
            }
        } else {
            throw new IllegalArgumentException("Unsupported parameter: " + parameter);
        }
    }

    public Trigger createTrigger(TriggerRequest request) {
        User user = userRepository.findUserByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Location location = locationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new RuntimeException("Location not found"));

        Trigger trigger = new Trigger();
        trigger.setName(request.getName());
        trigger.setParameter(ParameterType.fromString(request.getParameter()));
        trigger.setOperator(OperatorType.fromString(request.getOperator()));
        trigger.setValue(request.getValue());
        trigger.setLocation(location);
        trigger.setUser(user);

        return triggerRepository.save(trigger);
    }

    public void deleteTrigger(Long triggerId, String username) {
        Trigger trigger = triggerRepository.findById(triggerId)
                .orElseThrow(() -> new RuntimeException("Trigger not found"));

        if (!trigger.getUser().getUsername().equals(username)) {
            throw new RuntimeException("You are not allowed to delete this trigger");
        }

        triggerRepository.delete(trigger);
    }

    public List<Trigger> searchTriggersByName(String username) {
        List<Trigger> triggerList = triggerRepository.findAllByUserUsername(username);
        return triggerList;
    }
}
