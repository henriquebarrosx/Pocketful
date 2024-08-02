package com.pocketful.unit.util;

import com.pocketful.util.EmailValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)

public class EmailValidatorTest {
    @Test
    void shouldReturnTrueWhenStartsWithValidSignals() {
        String email = "john.doe+tag@example.com";
        Assertions.assertTrue(EmailValidator.validate(email));
    }

    @Test
    void shouldReturnTrueWhenDomainHasValidSignalsBeforeDot() {
        String email = "john.doe+tag@example-domain.com";
        Assertions.assertTrue(EmailValidator.validate(email));
    }

    @Test
    void shouldReturnTrueWhenDomainHasSubdomainAndMultipleDots() {
        String email = "john.doe+tag@example.domain.com";
        Assertions.assertTrue(EmailValidator.validate(email));
    }

    @Test
    void shouldReturnTrueWhenDomainEndsWithAlphabeticalTLD() {
        String email = "john.doe+tag@example.domain.org";
        Assertions.assertTrue(EmailValidator.validate(email));
    }

    @Test
    void shouldReturnTrueWhenDomainEndsWithInternationalCharacters() {
        String email = "john.doe+tag@example.domain.com.br";
        Assertions.assertTrue(EmailValidator.validate(email));
    }

    @Test
    void shouldReturnFalseWhenIsBlank() {
        Assertions.assertFalse(EmailValidator.validate(""));
    }

    @Test
    void shouldReturnFalseWhenDoesNotHaveDomain() {
        Assertions.assertFalse(EmailValidator.validate("john.doe"));
        Assertions.assertFalse(EmailValidator.validate("john.doe@"));
    }

    @Test
    void shouldReturnFalseWhenDomainHasSpecialCharacters() {
        Assertions.assertFalse(EmailValidator.validate("john.doe+tag@example例子"));
        Assertions.assertFalse(EmailValidator.validate("john.doe+tag@例子example.com"));

        Assertions.assertFalse(EmailValidator.validate("john.doe+tag@example!com"));
        Assertions.assertFalse(EmailValidator.validate("john.doe+tag@!example.com"));

        Assertions.assertFalse(EmailValidator.validate("john.doe+tag@example#com"));
        Assertions.assertFalse(EmailValidator.validate("john.doe+tag@#example#com"));
    }
}
