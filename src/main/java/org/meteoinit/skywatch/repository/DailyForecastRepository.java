package org.meteoinit.skywatch.repository;
import org.meteoinit.skywatch.model.DailyForecast;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DailyForecastRepository extends JpaRepository<DailyForecast, Long> {
    List<DailyForecast> findByLocationId(Long locationId);
    void deleteByLocationId(Long locationId);
}
