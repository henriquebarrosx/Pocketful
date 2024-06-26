package com.pocketful.repository;

import com.pocketful.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Boolean existsAccountByEmailOrPhoneNumber(String email, String phoneNumber);
}
