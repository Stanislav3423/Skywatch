package org.meteoinit.skywatch.service;


import org.meteoinit.skywatch.dto.ForecastDto;
import org.meteoinit.skywatch.dto.ForecastUpdateDto;
import org.meteoinit.skywatch.model.*;
import org.meteoinit.skywatch.repository.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ForecastService {

    private final WeatherApiService weatherApiService;
    private final HourlyForecastRepository hourlyRepo;
    private final DailyForecastRepository dailyRepo;
    private final DateRepository dateRepository;
    private final WindRepository windRepository;
    private final ConditionRepository conditionRepository;
    private final LocationRepository locationRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public void refreshForecasts(Long jsonLocationId) throws Exception {
        Location location = locationRepository.findLocationByIdJson(jsonLocationId)
                .orElseThrow(() -> new RuntimeException("Location not found"));
        Long locationId = location.getId();

        // 1. delete old forecasts
        hourlyRepo.deleteByLocationId(locationId);
        dailyRepo.deleteByLocationId(locationId);

        // 2. fetch new data
        String hourlyJson = weatherApiService.fetchHourlyForecast(jsonLocationId);
        String dailyJson = weatherApiService.fetchDailyForecast(jsonLocationId);

        JsonNode hourlyRoot = objectMapper.readTree(hourlyJson);
        JsonNode dailyRoot = objectMapper.readTree(dailyJson);


        // 3. process hourly forecast
        for (JsonNode node : hourlyRoot.get("list")) {
            HourlyForecast forecast = new HourlyForecast();
            forecast.setLocation(location);

            // ----- Date -----
            long epoch = node.get("dt").asLong();
            LocalDateTime gmt = LocalDateTime.ofInstant(Instant.ofEpochSecond(epoch), ZoneOffset.UTC);
            DateEntity dateEntity = new DateEntity();
            dateEntity.setEpoch(epoch);
            dateEntity.setGmt(gmt);
            dateEntity.setGmtPlus3(gmt.plusHours(3));
            dateEntity.setYear(gmt.getYear());
            dateEntity.setMonth(gmt.getMonthValue());
            dateEntity.setQuarter((gmt.getMonthValue()-1)/3 +1);
            dateEntity.setDay(gmt.getDayOfMonth());
            dateEntity.setHour(gmt.getHour());
            dateEntity.setWeekday(gmt.getDayOfWeek().toString());
            dateEntity = dateRepository.save(dateEntity);
            forecast.setDate(dateEntity);

            // ----- Wind -----
            JsonNode windNode = node.get("wind");
            Wind wind = new Wind();
            wind.setSpeed(windNode.get("speed").asDouble());
            wind.setDeg(windNode.get("deg").asInt());
            if (windNode.has("gust")) wind.setGust(windNode.get("gust").asDouble());
            wind = windRepository.save(wind);
            forecast.setWind(wind);

            // ----- Condition -----
            JsonNode conditionNode = node.get("weather").get(0);
            Long condId = conditionNode.get("id").asLong();
            Condition condition = conditionRepository.findById(condId)
                    .orElseGet(() -> {
                        Condition c = new Condition();
                        c.setId(condId);
                        c.setMain(conditionNode.get("main").asText());
                        c.setDescription(conditionNode.get("description").asText());
                        c.setIcon(conditionNode.get("icon").asText());
                        return conditionRepository.save(c);
                    });
            forecast.setCondition(condition);

            // ----- Main -----
            JsonNode mainNode = node.get("main");
            forecast.setTemp(mainNode.get("temp").asDouble());
            forecast.setFeelsLike(mainNode.get("feels_like").asDouble());
            forecast.setTempMin(mainNode.get("temp_min").asDouble());
            forecast.setTempMax(mainNode.get("temp_max").asDouble());
            forecast.setPressure(mainNode.get("pressure").asInt());
            forecast.setHumidity(mainNode.get("humidity").asInt());

            forecast.setCloudiness(node.get("clouds").get("all").asInt());

            if (node.has("rain") && node.get("rain").has("1h"))
                forecast.setPrecipitationAmount(node.get("rain").get("1h").asDouble());
            if (node.has("pop"))
                forecast.setPrecipitationProbability(node.get("pop").asDouble()*100);

            hourlyRepo.save(forecast);
        }

        // 4. process daily forecast
        for (JsonNode node : dailyRoot.get("list")) {
            DailyForecast forecast = new DailyForecast();
            forecast.setLocation(location);

            // ----- Date -----
            long epoch = node.get("dt").asLong();
            LocalDateTime gmt = LocalDateTime.ofInstant(Instant.ofEpochSecond(epoch), ZoneOffset.UTC);
            DateEntity dateEntity = new DateEntity();
            dateEntity.setEpoch(epoch);
            dateEntity.setGmt(gmt);
            dateEntity.setGmtPlus3(gmt.plusHours(3));
            dateEntity.setYear(gmt.getYear());
            dateEntity.setMonth(gmt.getMonthValue());
            dateEntity.setQuarter((gmt.getMonthValue()-1)/3 +1);
            dateEntity.setDay(gmt.getDayOfMonth());
            dateEntity.setHour(0);
            dateEntity.setWeekday(gmt.getDayOfWeek().toString());
            dateEntity = dateRepository.save(dateEntity);
            forecast.setDate(dateEntity);

            // ----- Wind -----
            Wind wind = new Wind();
            wind.setSpeed(node.get("speed").asDouble());
            wind.setDeg(node.get("deg").asInt());
            if (node.has("gust")) wind.setGust(node.get("gust").asDouble());
            wind = windRepository.save(wind);
            forecast.setWind(wind);

            // ----- Condition -----
            JsonNode conditionNode = node.get("weather").get(0);
            Long condId = conditionNode.get("id").asLong();
            Condition condition = conditionRepository.findById(condId)
                    .orElseGet(() -> {
                        Condition c = new Condition();
                        c.setId(condId);
                        c.setMain(conditionNode.get("main").asText());
                        c.setDescription(conditionNode.get("description").asText());
                        c.setIcon(conditionNode.get("icon").asText());
                        return conditionRepository.save(c);
                    });
            forecast.setCondition(condition);
            // ----- Main -----
            JsonNode tempNode = node.get("temp");
            JsonNode feelsLikeNode = node.get("feels_like");
            forecast.setTemp(tempNode.get("day").asDouble());
            forecast.setFeelsLike(feelsLikeNode.get("day").asDouble());
            forecast.setTempMin(tempNode.get("min").asDouble());
            forecast.setTempMax(tempNode.get("max").asDouble());
            forecast.setPressure(node.get("pressure").asInt());
            forecast.setHumidity(node.get("humidity").asInt());

            forecast.setCloudiness(node.get("clouds").asInt());

            if (node.has("rain"))
                forecast.setPrecipitationAmount(node.get("rain").asDouble());
            if (node.has("pop"))
                forecast.setPrecipitationProbability(node.get("pop").asDouble()*100);

            dailyRepo.save(forecast);
        }
    }

    public List<ForecastDto> get7DayForecast(Long locationId) {
        List<DailyForecast> forecasts = dailyRepo.findDailyForecastsByLocation_IdJson(locationId);
        return forecasts.stream().map(this::mapDailyForecastToDto).collect(Collectors.toList());
    }

    public List<ForecastDto> get12HourForecast(Long locationId) {
        List<HourlyForecast> forecasts = hourlyRepo.findHourlyForecastsByLocation_IdJson(locationId);
        return forecasts.stream().map(this::mapHourlyForecastToDto).collect(Collectors.toList());
    }

    public ForecastDto mapDailyForecastToDto(DailyForecast forecast) {
        ForecastDto dto = new ForecastDto();
        dto.setId(forecast.getId());
        dto.setDayOrHour(forecast.getDate().getWeekday());
        dto.setDate(forecast.getDate().getGmt().toLocalDate().toString());
        dto.setIcon(forecast.getCondition().getIcon());
        dto.setTemperature((float) forecast.getTemp());
        dto.setHumidity(forecast.getHumidity());
        dto.setPressure(forecast.getPressure());
        dto.setWind_dir(forecast.getWind().getDeg());
        dto.setWind_speed(forecast.getWind().getSpeed().floatValue());
        dto.setPrecipitation_amount(forecast.getPrecipitationAmount() != null ? forecast.getPrecipitationAmount().floatValue() : 0);
        dto.setPrecipitation_probability(forecast.getPrecipitationProbability() != null ? forecast.getPrecipitationProbability().floatValue() : 0);
        dto.setCondition(forecast.getCondition().getMain());
        return dto;
    }


    public ForecastDto mapHourlyForecastToDto(HourlyForecast forecast) {
        ForecastDto dto = new ForecastDto();
        dto.setId(forecast.getId());
        dto.setDayOrHour(String.format("%02d:00", forecast.getDate().getHour()));
        dto.setDate(forecast.getDate().getGmt().toLocalDate().toString());
        dto.setIcon(forecast.getCondition().getIcon());
        dto.setTemperature((float) forecast.getTemp());
        dto.setPressure(forecast.getPressure());
        dto.setHumidity(forecast.getHumidity());
        dto.setWind_dir(forecast.getWind().getDeg());
        dto.setWind_speed(forecast.getWind().getSpeed().floatValue());
        dto.setPrecipitation_amount(forecast.getPrecipitationAmount() != null ? forecast.getPrecipitationAmount().floatValue() : 0);
        dto.setPrecipitation_probability(forecast.getPrecipitationProbability() != null ? forecast.getPrecipitationProbability().floatValue() : 0);
        dto.setCondition(forecast.getCondition().getMain());
        return dto;
    }

    @Transactional
    public void updateDailyForecast(ForecastUpdateDto dto) {
        DailyForecast forecast = dailyRepo.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Daily forecast not found"));

        forecast.setTemp(dto.getTemperature());
        forecast.setPressure(dto.getPressure());
        forecast.setPrecipitationAmount(dto.getPrecipitationAmount());
        forecast.setPrecipitationProbability(dto.getPrecipitationProbability());
        Wind wind = forecast.getWind();
        wind.setSpeed(dto.getWindSpeed());
        wind.setDeg(dto.getWindDeg());
        windRepository.save(wind);
        dailyRepo.save(forecast);
    }

    @Transactional
    public void updateHourlyForecast(ForecastUpdateDto dto) {
        HourlyForecast forecast = hourlyRepo.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Daily forecast not found"));

        forecast.setTemp(dto.getTemperature());
        forecast.setPressure(dto.getPressure());
        forecast.setPrecipitationAmount(dto.getPrecipitationAmount());
        forecast.setPrecipitationProbability(dto.getPrecipitationProbability());
        Wind wind = forecast.getWind();
        wind.setSpeed(dto.getWindSpeed());
        wind.setDeg(dto.getWindDeg());
        windRepository.save(wind);
        hourlyRepo.save(forecast);
    }
}
