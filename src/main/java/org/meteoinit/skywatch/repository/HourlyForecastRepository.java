package org.meteoinit.skywatch.repository;
import org.meteoinit.skywatch.model.DailyForecast;
import org.meteoinit.skywatch.model.HourlyForecast;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HourlyForecastRepository extends JpaRepository<HourlyForecast, Long> {
    List<HourlyForecast> findHourlyForecastsByLocation_IdJson(Long idJson);
    void deleteByLocationId(Long locationId);

    /*@Query("""
        SELECT df FROM HourlyForecast df
        WHERE df.location.id = :locationId
        AND (
            (:parameter = 'temperature' AND 
                (CASE WHEN :operator = '>' THEN df.temp > :value
                      WHEN :operator = '<' THEN df.temp < :value
                      ELSE df.temp = :value END))
            OR
            (:parameter = 'humidity' AND 
                (CASE WHEN :operator = '>' THEN df.humidity > :value
                      WHEN :operator = '<' THEN df.humidity < :value
                      ELSE df.humidity = :value END))
            OR
            (:parameter = 'precipitation' AND 
                (CASE WHEN :operator = '>' THEN df.precipitationAmount > :value
                      WHEN :operator = '<' THEN df.precipitationAmount < :value
                      ELSE df.precipitationAmount = :value END))
        )
    """)
    List<HourlyForecast> findAllByTriggerCondition(@Param("parameter") String parameter,
                                                  @Param("operator") String operator,
                                                  @Param("value") double value,
                                                  @Param("locationId") Long locationId);*/


    @Query("""
    SELECT df FROM HourlyForecast df
    WHERE df.location.id = :locationId
      AND df.temp > :value
""")
    List<HourlyForecast> findTemperatureGreater(@Param("locationId") Long locationId, @Param("value") double value);

    @Query("""
    SELECT df FROM HourlyForecast df
    WHERE df.location.id = :locationId
      AND df.temp < :value
""")
    List<HourlyForecast> findTemperatureLess(@Param("locationId") Long locationId, @Param("value") double value);

    @Query("""
    SELECT df FROM HourlyForecast df
    WHERE df.location.id = :locationId
      AND df.temp = :value
""")
    List<HourlyForecast> findTemperatureEqual(@Param("locationId") Long locationId, @Param("value") double value);

    // Humidity
    @Query("""
    SELECT df FROM HourlyForecast df
    WHERE df.location.id = :locationId
      AND df.humidity > :value
""")
    List<HourlyForecast> findHumidityGreater(@Param("locationId") Long locationId, @Param("value") double value);

    @Query("""
    SELECT df FROM HourlyForecast df
    WHERE df.location.id = :locationId
      AND df.humidity < :value
""")
    List<HourlyForecast> findHumidityLess(@Param("locationId") Long locationId, @Param("value") double value);

    @Query("""
    SELECT df FROM HourlyForecast df
    WHERE df.location.id = :locationId
      AND df.humidity = :value
""")
    List<HourlyForecast> findHumidityEqual(@Param("locationId") Long locationId, @Param("value") double value);

    // Precipitation
    @Query("""
    SELECT df FROM HourlyForecast df
    WHERE df.location.id = :locationId
      AND df.precipitationAmount > :value
""")
    List<HourlyForecast> findPrecipitationGreater(@Param("locationId") Long locationId, @Param("value") double value);

    @Query("""
    SELECT df FROM HourlyForecast df
    WHERE df.location.id = :locationId
      AND df.precipitationAmount < :value
""")
    List<HourlyForecast> findPrecipitationLess(@Param("locationId") Long locationId, @Param("value") double value);

    @Query("""
    SELECT df FROM HourlyForecast df
    WHERE df.location.id = :locationId
      AND df.precipitationAmount = :value
""")
    List<HourlyForecast> findPrecipitationEqual(@Param("locationId") Long locationId, @Param("value") double value);

}