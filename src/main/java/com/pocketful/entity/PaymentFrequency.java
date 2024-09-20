package com.pocketful.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;

@Table(name = "payment_frequencies")
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentFrequency {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payment_frequencies_seq_gen")
    @SequenceGenerator(name = "payment_frequencies_seq_gen", sequenceName = "payment_frequencies_seq", allocationSize = 1)
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
