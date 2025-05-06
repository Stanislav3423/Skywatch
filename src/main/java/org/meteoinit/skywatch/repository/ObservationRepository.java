package org.meteoinit.skywatch.repository;

import org.meteoinit.skywatch.dto.HumidityDto;
import org.meteoinit.skywatch.dto.PressureDto;
import org.meteoinit.skywatch.dto.TemperatureDto;
import org.meteoinit.skywatch.dto.WindDirectionDto;
import org.meteoinit.skywatch.model.Observation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ObservationRepository extends JpaRepository<Observation, Integer> {

    // Temperature
    @Query("""
        SELECT new org.meteoinit.skywatch.dto.TemperatureDto(
            d.year, d.month, d.day, d.hour, AVG(o.temp)
        )
        FROM Observation o
        JOIN o.date d
        WHERE (:locationId IS NULL OR o.location.id = :locationId)
          AND d.gmt >= :startDate AND d.gmt <= :endDate
        GROUP BY d.year, d.month, d.day, d.hour
        ORDER BY d.year, d.month, d.day, d.hour
    """)
    List<TemperatureDto> getHourlyTemperature(@Param("locationId") Long locationId,
                                              @Param("startDate") LocalDateTime startDate,
                                              @Param("endDate") LocalDateTime endDate);

    @Query("""
        SELECT new org.meteoinit.skywatch.dto.TemperatureDto(
            d.year, d.month, d.day, AVG(o.temp)
        )
        FROM Observation o
        JOIN o.date d
        WHERE (:locationId IS NULL OR o.location.id = :locationId)
          AND d.gmt >= :startDate AND d.gmt <= :endDate
        GROUP BY d.year, d.month, d.day
        ORDER BY d.year, d.month, d.day
    """)
    List<TemperatureDto> getDailyTemperature(@Param("locationId") Long locationId,
                                             @Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate);

    @Query("""
        SELECT new org.meteoinit.skywatch.dto.TemperatureDto(
            d.year, d.month, AVG(o.temp)
        )
        FROM Observation o
        JOIN o.date d
        WHERE (:locationId IS NULL OR o.location.id = :locationId)
          AND d.gmt >= :startDate AND d.gmt <= :endDate
        GROUP BY d.year, d.month
        ORDER BY d.year, d.month
    """)
    List<TemperatureDto> getMonthlyTemperature(@Param("locationId") Long locationId,
                                               @Param("startDate") LocalDateTime startDate,
                                               @Param("endDate") LocalDateTime endDate);

    // WIND
    @Query("""
        SELECT new org.meteoinit.skywatch.dto.WindDirectionDto(w.deg, AVG(w.speed))
        FROM Observation o
        JOIN o.wind w
        JOIN o.date d
        WHERE (:locationId IS NULL OR o.location.id = :locationId)
          AND d.gmt >= :startDate AND d.gmt <= :endDate
        GROUP BY w.deg
    """)
    List<WindDirectionDto> getWindAnalysis(@Param("locationId") Long locationId,
                                           @Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);

    // Pressure
    @Query("""
        SELECT new org.meteoinit.skywatch.dto.PressureDto(
            d.year, d.month, d.day, d.hour, AVG(o.pressure)
        )
        FROM Observation o
        JOIN o.date d
        WHERE (:locationId IS NULL OR o.location.id = :locationId)
          AND d.gmt >= :startDate AND d.gmt <= :endDate
        GROUP BY d.year, d.month, d.day, d.hour
        ORDER BY d.year, d.month, d.day, d.hour
    """)
    List<PressureDto> getHourlyPressure(@Param("locationId") Long locationId,
                                        @Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate);

    @Query("""
        SELECT new org.meteoinit.skywatch.dto.PressureDto(
            d.year, d.month, d.day, AVG(o.pressure)
        )
        FROM Observation o
        JOIN o.date d
        WHERE (:locationId IS NULL OR o.location.id = :locationId)
          AND d.gmt >= :startDate AND d.gmt <= :endDate
        GROUP BY d.year, d.month, d.day
        ORDER BY d.year, d.month, d.day
    """)
    List<PressureDto> getDailyPressure(@Param("locationId") Long locationId,
                                       @Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);

    @Query("""
        SELECT new org.meteoinit.skywatch.dto.PressureDto(
            d.year, d.month, AVG(o.pressure)
        )
        FROM Observation o
        JOIN o.date d
        WHERE (:locationId IS NULL OR o.location.id = :locationId)
          AND d.gmt >= :startDate AND d.gmt <= :endDate
        GROUP BY d.year, d.month
        ORDER BY d.year, d.month
    """)
    List<PressureDto> getMonthlyPressure(@Param("locationId") Long locationId,
                                         @Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate);

    // Humidity
    @Query("""
        SELECT new org.meteoinit.skywatch.dto.HumidityDto(
            d.year, d.month, d.day, d.hour, AVG(o.humidity)
        )
        FROM Observation o
        JOIN o.date d
        WHERE (:locationId IS NULL OR o.location.id = :locationId)
          AND d.gmt >= :startDate AND d.gmt <= :endDate
        GROUP BY d.year, d.month, d.day, d.hour
        ORDER BY d.year, d.month, d.day, d.hour
    """)
    List<HumidityDto> getHourlyHumidity(@Param("locationId") Long locationId,
                                        @Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate);

    @Query("""
        SELECT new org.meteoinit.skywatch.dto.HumidityDto(
            d.year, d.month, d.day, AVG(o.humidity)
        )
        FROM Observation o
        JOIN o.date d
        WHERE (:locationId IS NULL OR o.location.id = :locationId)
          AND d.gmt >= :startDate AND d.gmt <= :endDate
        GROUP BY d.year, d.month, d.day
        ORDER BY d.year, d.month, d.day
    """)
    List<HumidityDto> getDailyHumidity(@Param("locationId") Long locationId,
                                       @Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);

    @Query("""
        SELECT new org.meteoinit.skywatch.dto.HumidityDto(
            d.year, d.month, AVG(o.humidity)
        )
        FROM Observation o
        JOIN o.date d
        WHERE (:locationId IS NULL OR o.location.id = :locationId)
          AND d.gmt >= :startDate AND d.gmt <= :endDate
        GROUP BY d.year, d.month
        ORDER BY d.year, d.month
    """)
    List<HumidityDto> getMonthlyHumidity(@Param("locationId") Long locationId,
                                         @Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate);
    // Average
    @Query("""
        SELECT AVG(o.temp) FROM Observation o
        JOIN o.date d
        WHERE (:locationId IS NULL OR o.location.id = :locationId)
          AND d.gmt >= :startDate AND d.gmt <= :endDate
    """)
    Double getAverageTemperature(@Param("locationId") Long locationId,
                                 @Param("startDate") LocalDateTime startDate,
                                 @Param("endDate") LocalDateTime endDate);

    @Query("""
        SELECT AVG(o.humidity) FROM Observation o
        JOIN o.date d
        WHERE (:locationId IS NULL OR o.location.id = :locationId)
          AND d.gmt >= :startDate AND d.gmt <= :endDate
    """)
    Double getAverageHumidity(@Param("locationId") Long locationId,
                              @Param("startDate") LocalDateTime startDate,
                              @Param("endDate") LocalDateTime endDate);

    @Query("""
        SELECT AVG(o.pressure) FROM Observation o
        JOIN o.date d
        WHERE (:locationId IS NULL OR o.location.id = :locationId)
          AND d.gmt >= :startDate AND d.gmt <= :endDate
    """)
    Double getAveragePressure(@Param("locationId") Long locationId,
                              @Param("startDate") LocalDateTime startDate,
                              @Param("endDate") LocalDateTime endDate);

    @Query("""
        SELECT AVG(o.wind.speed) FROM Observation o
        JOIN o.wind
        JOIN o.date d
        WHERE (:locationId IS NULL OR o.location.id = :locationId)
          AND d.gmt >= :startDate AND d.gmt <= :endDate
    """)
    Double getAverageWindSpeed(@Param("locationId") Long locationId,
                               @Param("startDate") LocalDateTime startDate,
                               @Param("endDate") LocalDateTime endDate);


    @Query("""
        SELECT MAX(o.temp) FROM Observation o
        JOIN o.date d
        WHERE (:locationId IS NULL OR o.location.id = :locationId)
          AND d.gmt >= :startDate AND d.gmt <= :endDate
    """)
    Double getMaxTemperature(@Param("locationId") Long locationId,
                             @Param("startDate") LocalDateTime startDate,
                             @Param("endDate") LocalDateTime endDate);

    @Query("""
        SELECT MIN(o.temp) FROM Observation o
        JOIN o.date d
        WHERE (:locationId IS NULL OR o.location.id = :locationId)
          AND d.gmt >= :startDate AND d.gmt <= :endDate
    """)
    Double getMinTemperature(@Param("locationId") Long locationId,
                             @Param("startDate") LocalDateTime startDate,
                             @Param("endDate") LocalDateTime endDate);

    @Query("""
        SELECT AVG(o.cloudiness) FROM Observation o
        JOIN o.date d
        WHERE (:locationId IS NULL OR o.location.id = :locationId)
          AND d.gmt >= :startDate AND d.gmt <= :endDate
    """)
    Double getAverageCloudiness(@Param("locationId") Long locationId,
                                @Param("startDate") LocalDateTime startDate,
                                @Param("endDate") LocalDateTime endDate);

}
