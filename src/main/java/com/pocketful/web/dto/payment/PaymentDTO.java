package com.pocketful.web.dto.payment;

import com.pocketful.entity.PaymentCategory;
import com.pocketful.entity.PaymentFrequency;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDTO {
    private Long id;
    private float amount;
    private String description;
    private Boolean payed;
    private Boolean isExpense;
    private LocalDate deadlineAt;
    private PaymentCategory paymentCategory;
    private PaymentFrequency paymentFrequency;
}
