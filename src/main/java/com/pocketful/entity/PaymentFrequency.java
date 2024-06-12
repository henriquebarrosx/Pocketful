package com.pocketful.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;

@Table(name = "payment_frequencies")
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentFrequency {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column
    private Integer times;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDate createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDate updatedAt;
}
