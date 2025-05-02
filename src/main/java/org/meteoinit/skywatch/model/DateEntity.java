package org.meteoinit.skywatch.model;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Date")
@Data
public class DateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long epoch;

    private java.time.LocalDateTime gmt;

    @Column(name = "gmt_plus3")
    private java.time.LocalDateTime gmtPlus3;

    private Integer year;
    private Integer month;
    private Integer quarter;
    private Integer day;
    private Integer hour;
    private String weekday;
}