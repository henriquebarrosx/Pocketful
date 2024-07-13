package com.pocketful.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(name = "payments")
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
    private BigDecimal amount;

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
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
