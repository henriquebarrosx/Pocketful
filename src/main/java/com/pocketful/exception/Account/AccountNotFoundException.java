package com.pocketful.exception.Account;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(Long id) {
        super("Account not found");
    }

    public AccountNotFoundException(String email) {
        super("Account not found");
    }
}
