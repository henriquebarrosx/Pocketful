package com.pocketful.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;

@Table(name = "payments")
@Entity
@Getter
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

    @ManyToOne
    @JoinColumn(nullable = false)
    private Account account;

    @JoinColumn(nullable = false)
    @ManyToOne
    private PaymentCategory paymentCategory;

    @JoinColumn(nullable = false)
    @ManyToOne
    private PaymentFrequency paymentFrequency;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDate createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDate updatedAt;
}
