package com.pocketful.service;

import com.pocketful.entity.Account;
import com.pocketful.entity.AccountRole;
import com.pocketful.exception.EmailOrPhoneNumberAlreadyExistException;
import com.pocketful.exception.InvalidPhoneNumberException;
import com.pocketful.repository.AccountRepository;
import com.pocketful.utils.AccountBuilder;
import com.pocketful.utils.NewAccountRequestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AccountServiceTest {

    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldThrowExceptionWhenCreatingAccountUsingExistentEmailOrPhoneNumber() {
        var request = NewAccountRequestBuilder.build();

        Mockito.when(accountRepository.existsAccountByEmailOrPhoneNumber(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
            .thenReturn(true);

        Exception exception = assertThrows(EmailOrPhoneNumberAlreadyExistException.class, () -> accountService.create(request));
        String expected = String.format("Account using email %s or phone number %s already exists.", request.getEmail(), request.getPhoneNumber());
        String actual = exception.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldThrowExceptionWhenCreatingAccountUsingBadFormattedPhoneNumber() {
        var badFormattedPhoneNumber = "82900112233";
        var request = NewAccountRequestBuilder.build(badFormattedPhoneNumber);

        Mockito.when(accountRepository.existsAccountByEmailOrPhoneNumber(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
            .thenReturn(false);

        Exception exception = assertThrows(InvalidPhoneNumberException.class, () -> accountService.create(request));
        String expected = "Invalid account phone number. Please, provide valid country (Ex.: +55) and DDD number (Ex: 82).";
        String actual = exception.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnAccountWhenCreatingNewOneSuccessfully() {
        var request = NewAccountRequestBuilder.build();

        Mockito.when(accountRepository.existsAccountByEmailOrPhoneNumber(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
            .thenReturn(false);

        Mockito.when(accountRepository.save(ArgumentMatchers.any(Account.class)))
            .thenReturn(AccountBuilder.build());

        Account actual = accountService.create(request);

        assertEquals(1L, actual.getId());
        assertEquals("John Doe", actual.getName());
        assertEquals("john.doe@mail.com", actual.getEmail());
        assertEquals("+5582988779900", actual.getPhoneNumber());
        assertEquals(AccountRole.ADMIN, actual.getRole());
    }
}
