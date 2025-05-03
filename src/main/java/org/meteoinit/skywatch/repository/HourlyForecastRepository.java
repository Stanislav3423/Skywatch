package org.meteoinit.skywatch.repository;
import org.meteoinit.skywatch.model.HourlyForecast;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HourlyForecastRepository extends JpaRepository<HourlyForecast, Long> {
    List<HourlyForecast> findHourlyForecastsByLocation_IdJson(Long idJson);
    void deleteByLocationId(Long locationId);
}