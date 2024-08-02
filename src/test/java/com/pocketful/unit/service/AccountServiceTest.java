package com.pocketful.unit.service;

import com.pocketful.entity.Account;
import com.pocketful.exception.Account.AccountEmailAlreadyRegisteredException;
import com.pocketful.exception.Account.AccountNotFoundException;
import com.pocketful.exception.Account.InvalidAccountEmailFormatException;
import com.pocketful.repository.AccountRepository;
import com.pocketful.service.AccountService;
import com.pocketful.utils.AccountBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {
    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepository;

    @Test
    void shouldThrowAnExceptionWhenCreatingAccountUsingInvalidEmail() {
        String name = "John Doe";
        String email = "john.doe";
        String password = "00000000";

        Exception exception = Assertions.assertThrows(
                InvalidAccountEmailFormatException.class,
                () -> accountService.create(name, email, password)
        );

        Assertions.assertEquals("Invalid email format", exception.getMessage());
    }

    @Test
    void shouldThrowAnExceptionWhenCreatingAccountUsingExistentEmail() {
        String name = "John Doe";
        String email = "john.doe@mail.com";
        String password = "00000000";

        Mockito.when(accountRepository.existsByEmail(ArgumentMatchers.anyString()))
                .thenReturn(true);

        Exception exception = Assertions.assertThrows(
                AccountEmailAlreadyRegisteredException.class,
                () -> accountService.create(name, email, password)
        );

        Assertions.assertEquals(String.format("Account email %s already exists.", email), exception.getMessage());
    }

    @Test
    void shouldReturnAnAccountWhenHaveValidAndNotIsNotAlreadyRegisteredEmail() {
        Account account = AccountBuilder.build();

        Mockito.when(accountRepository.existsByEmail(ArgumentMatchers.anyString()))
                .thenReturn(false);

        Mockito.when(accountRepository.save(ArgumentMatchers.any(Account.class)))
                .thenReturn(account);

        Account result = accountService.create(account.getName(), account.getEmail(), account.getPassword());
        Assertions.assertEquals(account.getId(), result.getId());
    }

    @Test
    void shouldThrowAnExceptionWhenFindNonExistentAccountById() {
        Long id = 1L;

        Mockito.when(accountRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(
                AccountNotFoundException.class,
                () -> accountService.findById(id)
        );

        Assertions.assertEquals(String.format("Account by id %s not found", id), exception.getMessage());
    }

    @Test
    void shouldReturnAnAccountWhenFindExistentAccountById() {
        Account account = AccountBuilder.build();

        Mockito.when(accountRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(account));

        Account result = accountService.findById(account.getId());
        Assertions.assertEquals(account.getId(), result.getId());
    }

    @Test
    void shouldThrowAnExceptionWhenFindNonExistentAccountByEmail() {
        String email = "john.doe@mail.com";

        Mockito.when(accountRepository.findByEmail(ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(
                AccountNotFoundException.class,
                () -> accountService.findByEmail(email)
        );

        Assertions.assertEquals(String.format("Account by email %s not found", email), exception.getMessage());
    }

    @Test
    void shouldReturnAnAccountWhenFindExistentAccountByEmail() {
        Account account = AccountBuilder.build();

        Mockito.when(accountRepository.findByEmail(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(account));

        Account result = accountService.findByEmail(account.getEmail());
        Assertions.assertEquals(account.getId(), result.getId());
        Assertions.assertEquals(account.getEmail(), result.getEmail());
    }
}
