package com.pocketful.exception;

import com.pocketful.exception.Account.AccountEmailAlreadyRegisteredException;
import com.pocketful.exception.Account.AccountNotFoundException;
import com.pocketful.exception.Account.InvalidCredentialsException;
import com.pocketful.exception.Payment.InvalidPaymentAmountException;
import com.pocketful.exception.Payment.PaymentNotFoundException;
import com.pocketful.exception.PaymentCategory.PaymentCategoryAlreadyExistException;
import com.pocketful.exception.PaymentCategory.PaymentCategoryNotFoundException;
import com.pocketful.exception.PaymentFrequency.InvalidFrequencyTimesException;
import com.pocketful.exception.PaymentFrequency.PaymentFrequencyNotFoundException;
import com.pocketful.web.dto.exception.ExceptionDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AccountEmailAlreadyRegisteredException.class)
    public ResponseEntity<ExceptionDTO> handleEmailOrPhoneNumberAlreadyExistException(Exception exception) {
        ExceptionDTO exceptionDTO = new ExceptionDTO(exception.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exceptionDTO);
    }

    @ExceptionHandler(PaymentCategoryAlreadyExistException.class)
    public ResponseEntity<ExceptionDTO> handlePaymentCategoryAlreadyExistException(Exception exception) {
        ExceptionDTO exceptionDTO = new ExceptionDTO(exception.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exceptionDTO);
    }


    @ExceptionHandler(InvalidPaymentAmountException.class)
    public ResponseEntity<ExceptionDTO> handleInvalidPaymentAmountException(Exception exception) {
        ExceptionDTO exceptionDTO = new ExceptionDTO(exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionDTO);
    }

    @ExceptionHandler(InvalidFrequencyTimesException.class)
    public ResponseEntity<ExceptionDTO> handleInvalidFrequencyTimesException(Exception exception) {
        ExceptionDTO exceptionDTO = new ExceptionDTO(exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionDTO);
    }

    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity<ExceptionDTO> handlePaymentNotFoundException(Exception exception) {
        ExceptionDTO exceptionDTO = new ExceptionDTO(exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionDTO);
    }

    @ExceptionHandler(PaymentCategoryNotFoundException.class)
    public ResponseEntity<ExceptionDTO> handlePaymentCategoryNotFoundException(Exception exception) {
        ExceptionDTO exceptionDTO = new ExceptionDTO(exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionDTO);
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ExceptionDTO> handleAccountNotFoundException(Exception exception) {
        ExceptionDTO exceptionDTO = new ExceptionDTO(exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionDTO);
    }

    @ExceptionHandler(PaymentFrequencyNotFoundException.class)
    public ResponseEntity<ExceptionDTO> handlePaymentFrequencyNotFoundException(Exception exception) {
        ExceptionDTO exceptionDTO = new ExceptionDTO(exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionDTO);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ExceptionDTO> handleInvalidCredentialsException(Exception exception) {
        ExceptionDTO exceptionDTO = new ExceptionDTO(exception.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(exceptionDTO);
    }
}
