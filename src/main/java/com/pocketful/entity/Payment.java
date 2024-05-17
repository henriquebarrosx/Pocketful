package com.pocketful.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Table
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private float amount;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Boolean payed;

    @Column(nullable = false)
    private Boolean isExpense;

    @Column
    private LocalDate deadlineAt;

    @JoinColumn(nullable = false)
    @ManyToOne
    private PaymentCategory paymentCategory;

    @JoinColumn(nullable = false)
    @ManyToOne
    private PaymentFrequency paymentFrequency;

    @Column(nullable = false)
    private LocalDate createdAt;

    @Column(nullable = false)
    private LocalDate updatedAt;
}
