package com.yourcompany.surveys.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
public class Address {
    @Id
    private Long id;
    private String direction;

    @ManyToOne (
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}
    )
    @JoinColumn (
            name = "city_id"
    )
    @JsonManagedReference
    private City city;

    @ManyToOne (
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "user_id"
    )
    @JsonBackReference
    User user;
}