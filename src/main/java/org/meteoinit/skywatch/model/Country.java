package org.meteoinit.skywatch.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Countries")
@Data
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "code", nullable = false, unique = true)
    private String code;
}
