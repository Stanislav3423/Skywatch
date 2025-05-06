package org.meteoinit.skywatch.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "Reports")
@Data
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String location;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(name = "start_date")
    private LocalDateTime startDate;
    @Column(name = "end_date")
    private LocalDateTime endDate;
    @Column(name = "avg_temp")
    private Double avgTemp;
    @Column(name = "avg_humidity")
    private Double avgHumidity;
    @Column(name = "avg_pressure")
    private Double avgPressure;
    @Column(name = "avg_wind_speed")
    private Double avgWindSpeed;
    @Column(name = "max_temp")
    private Double maxTemp;
    @Column(name = "min_temp")
    private Double minTemp;
    @Column(name = "avg_cloudiness")
    private Double avgCloudiness;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}