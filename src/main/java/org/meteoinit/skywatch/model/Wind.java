package org.meteoinit.skywatch.model;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Wind")
@Data
public class Wind {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double speed;
    private Integer deg;
    private Double gust;
}