package org.meteoinit.skywatch.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "UserLocations")
@Data
public class UserLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

}