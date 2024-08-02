package com.pocketful.exception.Account;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(Long id) {
        super(String.format("Account by id %s not found", id));
    }

    public AccountNotFoundException(String email) {
        super(String.format("Account by email %s not found", email));
    }
}
