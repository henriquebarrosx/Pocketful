package com.pocketful.repository;

import com.pocketful.entity.Account;
import com.pocketful.entity.Payment;
import com.pocketful.entity.PaymentFrequency;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("SELECT payment " +
        "FROM Payment payment " +
        "WHERE payment.deadlineAt <= :date " +
        "AND payment.payed = false " +
        "AND payment.isExpense = true")
    List<Payment> findPendingBetweenNowAndDate(LocalDate date);

    @Query("SELECT payment " +
        "FROM Payment payment " +
        "WHERE payment.deadlineAt >= :startAt " +
        "AND payment.deadlineAt <= :endAt " +
        "AND payment.account = :account")
    List<Payment> findByDeadlineAtBetween(
        Account account,
        LocalDate startAt,
        LocalDate endAt);

    @Query("SELECT payment " +
            "FROM Payment payment " +
            "WHERE payment.deadlineAt >= :date " +
            "AND payment.account.id = :accountId")
    List<Payment> findByDeadlineAtGreaterThanOrEqual(Long accountId, LocalDate date);

    List<Payment> findAllByPaymentFrequency(PaymentFrequency paymentFrequency);

    boolean existsPaymentByPaymentFrequency(PaymentFrequency paymentFrequency);

    @Modifying
    @Transactional
    @Query("DELETE FROM Payment " +
            "WHERE paymentFrequency.id = :frequencyId " +
            "AND account.id = :accountId " +
            "AND deadlineAt >= :deadlineAt")
    void deleteByDeadlineAtBetweenPresentAndFuture(Long frequencyId,
                                                   Long accountId,
                                                   LocalDate deadlineAt);

    void deleteAllByPaymentFrequency(PaymentFrequency paymentFrequency);
}
