package org.meteoinit.skywatch.model;

import lombok.Data;
import jakarta.persistence.*;
import lombok.Data;
@Entity
@Table(name = "Observations")
@Data
public class Observation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "location_id", nullable = false, foreignKey = @ForeignKey(name = "FK_Observations_Locations"))
    private Location location;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "date_id", nullable = false, foreignKey = @ForeignKey(name = "FK_Observations_Date"))
    private DateEntity date;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "wind_id", nullable = false, foreignKey = @ForeignKey(name = "FK_Observations_Wind"))
    private Wind wind;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "condition_id", nullable = false, foreignKey = @ForeignKey(name = "FK_Observations_Conditions"))
    private Condition condition;

    @Column(name = "temp", nullable = false)
    private Double temp;

    @Column(name = "feels_like", nullable = false)
    private Double feelsLike;

    @Column(name = "pressure", nullable = false)
    private Integer pressure;

    @Column(name = "humidity", nullable = false)
    private Integer humidity;

    @Column(name = "cloudiness", nullable = false)
    private Integer cloudiness;
}
