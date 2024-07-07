package com.pocketful.controller;

import com.pocketful.entity.Account;
import com.pocketful.service.AccountService;
import com.pocketful.web.dto.account.AccountDTO;
import com.pocketful.web.mapper.AccountDTOMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("v1/accounts")
public class AccountController {
    private final AccountService accountService;
    private final AccountDTOMapper accountDTOMapper;

    @GetMapping
    public ResponseEntity<List<AccountDTO>> getAll() {
        log.info("Getting all accounts - START");

        List<AccountDTO> accounts = accountService.findAll().stream()
                .map(accountDTOMapper)
                .toList();

        log.info("Getting all accounts - END: {}", accounts);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(accounts);
    }

    @GetMapping("{id}")
    public ResponseEntity<AccountDTO> getBydId(@PathVariable Long id) {
        log.info("Getting an account by id {} - START", id);
        Account account = accountService.findById(id);
        log.info("Getting an account by id {} - END", account);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(accountDTOMapper.apply(account));
    }
}
