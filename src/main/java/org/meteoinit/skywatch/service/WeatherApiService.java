package org.meteoinit.skywatch.service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WeatherApiService {

    private final RestTemplate restTemplate = new RestTemplate();

    private final String API_KEY = "80a1073bb7f6eeb206b6511bed52044a";
    private final String BASE_URL = "https://pro.openweathermap.org/data/2.5";

    public String fetchHourlyForecast(Long cityId) {
        String url = UriComponentsBuilder.fromHttpUrl(BASE_URL + "/forecast/hourly")
                .queryParam("id", cityId)
                .queryParam("appid", API_KEY)
                .queryParam("units", "metric")
                .queryParam("cnt", 12)
                .toUriString();

        return restTemplate.getForObject(url, String.class);
    }

    public String fetchDailyForecast(Long cityId) {
        String url = UriComponentsBuilder.fromHttpUrl(BASE_URL + "/forecast/daily")
                .queryParam("id", cityId)
                .queryParam("appid", API_KEY)
                .queryParam("units", "metric")
                .queryParam("cnt", 7)
                .toUriString();

        return restTemplate.getForObject(url, String.class);
    }
}