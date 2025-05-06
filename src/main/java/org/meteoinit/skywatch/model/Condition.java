package org.meteoinit.skywatch.model;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Conditions")
@Data
public class Condition {

    @Id
    private Long id;
    private String main;
    private String description;
    private String icon;
}
