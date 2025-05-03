package org.meteoinit.skywatch.repository;
import org.meteoinit.skywatch.model.DailyForecast;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DailyForecastRepository extends JpaRepository<DailyForecast, Long> {
    List<DailyForecast> findDailyForecastsByLocation_IdJson(Long idJson);
    void deleteByLocationId(Long locationId);
}
