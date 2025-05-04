package org.meteoinit.skywatch.repository;
import org.meteoinit.skywatch.model.DailyForecast;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface DailyForecastRepository extends JpaRepository<DailyForecast, Long> {
    List<DailyForecast> findDailyForecastsByLocation_IdJson(Long idJson);
    void deleteByLocationId(Long locationId);

    /*@Query("""
        SELECT df FROM DailyForecast df
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
    List<DailyForecast> findAllByTriggerCondition(@Param("parameter") String parameter,
                                                  @Param("operator") String operator,
                                                  @Param("value") double value,
                                                  @Param("locationId") Long locationId);*/

    @Query("""
    SELECT df FROM DailyForecast df
    WHERE df.location.id = :locationId
      AND df.temp > :value
""")
    List<DailyForecast> findTemperatureGreater(@Param("locationId") Long locationId, @Param("value") double value);

    @Query("""
    SELECT df FROM DailyForecast df
    WHERE df.location.id = :locationId
      AND df.temp < :value
""")
    List<DailyForecast> findTemperatureLess(@Param("locationId") Long locationId, @Param("value") double value);

    @Query("""
    SELECT df FROM DailyForecast df
    WHERE df.location.id = :locationId
      AND df.temp = :value
""")
    List<DailyForecast> findTemperatureEqual(@Param("locationId") Long locationId, @Param("value") double value);

    // Humidity
    @Query("""
    SELECT df FROM DailyForecast df
    WHERE df.location.id = :locationId
      AND df.humidity > :value
""")
    List<DailyForecast> findHumidityGreater(@Param("locationId") Long locationId, @Param("value") double value);

    @Query("""
    SELECT df FROM DailyForecast df
    WHERE df.location.id = :locationId
      AND df.humidity < :value
""")
    List<DailyForecast> findHumidityLess(@Param("locationId") Long locationId, @Param("value") double value);

    @Query("""
    SELECT df FROM DailyForecast df
    WHERE df.location.id = :locationId
      AND df.humidity = :value
""")
    List<DailyForecast> findHumidityEqual(@Param("locationId") Long locationId, @Param("value") double value);

    // Precipitation
    @Query("""
    SELECT df FROM DailyForecast df
    WHERE df.location.id = :locationId
      AND df.precipitationAmount > :value
""")
    List<DailyForecast> findPrecipitationGreater(@Param("locationId") Long locationId, @Param("value") double value);

    @Query("""
    SELECT df FROM DailyForecast df
    WHERE df.location.id = :locationId
      AND df.precipitationAmount < :value
""")
    List<DailyForecast> findPrecipitationLess(@Param("locationId") Long locationId, @Param("value") double value);

    @Query("""
    SELECT df FROM DailyForecast df
    WHERE df.location.id = :locationId
      AND df.precipitationAmount = :value
""")
    List<DailyForecast> findPrecipitationEqual(@Param("locationId") Long locationId, @Param("value") double value);

}
