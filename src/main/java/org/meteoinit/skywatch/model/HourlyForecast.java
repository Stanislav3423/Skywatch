package org.meteoinit.skywatch.model;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "HourlyForecasts")
@Data
public class HourlyForecast {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @ManyToOne
    @JoinColumn(name = "date_id", nullable = false)
    private DateEntity date;

    private double temp;
    @Column(name = "feels_like")
    private double feelsLike;
    @Column(name = "temp_min")
    private double tempMin;
    @Column(name = "temp_max")
    private double tempMax;

    private int pressure;
    private int humidity;

    @ManyToOne
    @JoinColumn(name = "wind_id", nullable = false)
    private Wind wind;

    private int cloudiness;

    @ManyToOne
    @JoinColumn(name = "condition_id", nullable = false)
    private Condition condition;

    @Column(name = "precipitation_amount")
    private Double precipitationAmount;
    @Column(name = "precipitation_probability")
    private Double precipitationProbability;
}
