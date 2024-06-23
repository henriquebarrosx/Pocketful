package com.pocketful.repository;

import com.pocketful.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.time.LocalDate;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findAllByDeadlineAtLessThanEqualAndPayedIsFalseAndIsExpenseIsTrue(LocalDate date);
}
