package com.yourcompany.surveys.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class City {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;
    @Column (
            unique = true,
            nullable = false,
            length = 70
    )
    private String name;

    @ManyToOne (
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "state_id",
            nullable = false
    )
    @JsonManagedReference
    private State state;
}