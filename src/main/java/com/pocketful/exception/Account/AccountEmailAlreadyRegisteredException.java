package com.pocketful.exception.Account;

public class AccountEmailAlreadyRegisteredException extends RuntimeException {
    public AccountEmailAlreadyRegisteredException(String email) {
        super(String.format("Account email %s already exists.", email));
    }
}
