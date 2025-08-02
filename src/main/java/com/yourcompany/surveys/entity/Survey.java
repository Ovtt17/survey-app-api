package com.yourcompany.surveys.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "surveys")
public class Survey extends BaseAuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 500)
    private String description;

    private String pictureUrl;

    @Column(nullable = false, insertable = false, columnDefinition = "double(5,1) default 0.0")
    @Builder.Default
    private Double averageRating = 0.0;

    @Column(nullable = false, insertable = false, columnDefinition = "bigint default 0")
    @Builder.Default
    private Long ratingCount = 0L;

    @OneToMany(
            mappedBy = "survey",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},
            orphanRemoval = true
    )
    @Column(nullable = false)
    private List<Question> questions;

    @OneToMany(
            mappedBy = "survey",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}
    )
    private List<Review> reviews;

    @OneToMany(
            mappedBy = "survey",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},
            orphanRemoval = true
    )
    private List<Participation> participations;

    @OneToMany(
            mappedBy = "survey",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},
            orphanRemoval = true
    )
    private List<Rating> ratings;
}