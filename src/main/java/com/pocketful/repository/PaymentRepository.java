package com.pocketful.repository;

import com.pocketful.entity.Account;
import com.pocketful.entity.Payment;
import com.pocketful.entity.PaymentFrequency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findAllByDeadlineAtLessThanEqualAndPayedIsFalseAndIsExpenseIsTrue(LocalDate date);

    List<Payment> findAllByAccountAndDeadlineAtBetweenOrderByCreatedAtAsc(
        Account account,
        LocalDate startAt,
        LocalDate endAt
    );

    List<Payment> findAllByDeadlineAtGreaterThanEqual(LocalDate date);

    List<Payment> findAllByPaymentFrequency(PaymentFrequency paymentFrequency);

    boolean existsPaymentByPaymentFrequency(PaymentFrequency paymentFrequency);

    void deleteAllByDeadlineAtGreaterThanEqual(LocalDate date);

    void deleteAllByPaymentFrequency(PaymentFrequency paymentFrequency);
}
