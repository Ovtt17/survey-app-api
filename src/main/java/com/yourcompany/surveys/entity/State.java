package com.yourcompany.surveys.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "states")
public class State {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(
            unique = true,
            nullable = false,
            length = 50
    )
    private String name;

    @OneToMany (
            mappedBy = "state"
    )
    @JsonBackReference
    private Set<City> cities;
}