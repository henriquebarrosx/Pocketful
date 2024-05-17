package com.pocketful.repository;

import com.pocketful.entity.PaymentCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentCategoryRepository extends JpaRepository<PaymentCategory, Long> {}
