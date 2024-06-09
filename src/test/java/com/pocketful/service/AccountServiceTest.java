package com.pocketful.service;

import com.pocketful.entity.Account;
import com.pocketful.exception.ConflictException;
import com.pocketful.exception.NotFoundException;
import com.pocketful.repository.AccountRepository;
import com.pocketful.web.dto.account.NewAccountDTO;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AccountServiceTest {
    private AutoCloseable closeable;

    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepository;

    @BeforeEach
    public void setup() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    public void shouldGetAllAccounts() {
        List<Account> accounts = List.of(
            Account.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@mail.com")
                .phoneNumber("5582988776655")
                .build()
        );

        when(accountRepository.findAll())
            .thenReturn(accounts);

        assert(accountService.findAll())
            .equals(accounts);
    }

    @Test
    public void shouldThrowExceptionWhenGetNotExistentById() {
        when(accountRepository.findById(any(Long.class)))
            .thenReturn(Optional.empty());

        Exception exception = assertThrows(
            NotFoundException.class,
            () -> accountService.findById(1L)
        );

        assert(exception.getMessage()).equals("Account id do not exist.");
    }

    @Test
    void shouldThrowExceptionWhenCreateAccountUsingExistentEmail() {
        String EXISTENT_MAIL = "john.doe@mail.com";

        NewAccountDTO newAccountDTO = NewAccountDTO.builder()
            .name("John Doe")
            .email(EXISTENT_MAIL)
            .phoneNumber("5582988990011")
            .build();

        when(accountRepository.existsAccountByEmailOrPhoneNumber(EXISTENT_MAIL, newAccountDTO.getPhoneNumber()))
            .thenReturn(true);

        ConflictException exception = assertThrows(ConflictException.class, () -> accountService.create(newAccountDTO));
        assertEquals(exception.getMessage(), "An account with this email or phone number already exists.");
    }

    @Test
    void shouldThrowExceptionWhenCreateAccountUsingExistentPhoneNumber() {
        String EXISTENT_PHONE_NUMBER = "5582988990011";

        NewAccountDTO newAccountDTO = NewAccountDTO.builder()
            .name("John Doe")
            .phoneNumber(EXISTENT_PHONE_NUMBER)
            .email("john.doe@mail.com")
            .build();

        when(accountRepository.existsAccountByEmailOrPhoneNumber(newAccountDTO.getEmail(), EXISTENT_PHONE_NUMBER))
            .thenReturn(true);

        ConflictException exception = assertThrows(ConflictException.class, () -> accountService.create(newAccountDTO));
        assertEquals(exception.getMessage(), "An account with this email or phone number already exists.");
    }

    @Test
    void shouldCreateAnAccountWhenRegistrySuccessfullyUsingNonExistentEmailOrPhoneNumber() {
        Account actual = Account.builder()
            .id(1L)
            .name("John Doe")
            .email("john.doe@mail.com")
            .phoneNumber("5582988776655")
            .build();

        when(accountRepository.existsAccountByEmailOrPhoneNumber(any(String.class), any(String.class)))
            .thenReturn(false);

        when(accountRepository.save(any(Account.class)))
            .thenReturn(actual);

        Account expected = accountService
            .create(
                NewAccountDTO.builder()
                    .name(actual.getName())
                    .email(actual.getEmail())
                    .phoneNumber(actual.getPhoneNumber())
                    .build()
            );

        assertEquals(expected, actual);
    }
}