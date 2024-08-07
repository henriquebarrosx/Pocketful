package com.pocketful.repository;

import com.pocketful.entity.Account;
import com.pocketful.entity.Payment;
import com.pocketful.entity.PaymentFrequency;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findAllByDeadlineAtLessThanEqualAndPayedIsFalseAndIsExpenseIsTrue(LocalDate date);

    List<Payment> findAllByAccountAndDeadlineAtBetweenOrderByCreatedAtAsc(
        Account account,
        LocalDate startAt,
        LocalDate endAt
    );

    @Query("SELECT * " +
            "FROM payments " +
            "WHERE deadline_at >= :date " +
            "AND account_id = :accountId")
    List<Payment> findByAccountFromDeadline(Long accountId, LocalDate date);

    List<Payment> findAllByPaymentFrequency(PaymentFrequency paymentFrequency);

    boolean existsPaymentByPaymentFrequency(PaymentFrequency paymentFrequency);

    @Transactional
    @Query("DELETE FROM payments " +
            "WHERE payment_frequency_id = :frequencyId " +
            "AND account_id = :accountId " +
            "AND deadlineAt = :deadlineAt")
    void deleteCurrentAndFutureByAccount(PaymentFrequency paymentFrequency,
                                         Account account,
                                         LocalDate date);

    void deleteAllByPaymentFrequency(PaymentFrequency paymentFrequency);
}
