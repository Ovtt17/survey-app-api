package com.yourcompany.surveys.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "participations")
@EntityListeners(AuditingEntityListener.class)
public class Participation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "survey_id", nullable = false)
    private Survey survey;

    @OneToMany(mappedBy = "participation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Answer> answers;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime participatedDate;
}