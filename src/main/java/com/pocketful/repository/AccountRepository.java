package com.pocketful.repository;

import com.pocketful.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Boolean existsAccountByEmail(String email);
    Optional<Account> findAccountByEmail(String email);
}
