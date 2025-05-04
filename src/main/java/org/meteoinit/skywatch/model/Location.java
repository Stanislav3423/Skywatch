package org.meteoinit.skywatch.model;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "Locations")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Double lon;
    private Double lat;
    @Column(name="id_json")
    private Long idJson;
    private String state;

    @ManyToOne
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserLocation> userLocations = new ArrayList<>();

    @OneToMany(mappedBy = "location")
    private List<Trigger> triggers;
}
