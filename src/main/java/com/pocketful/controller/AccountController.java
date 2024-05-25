package com.pocketful.controller;

import com.pocketful.entity.Account;
import com.pocketful.service.AccountService;
import com.pocketful.web.dto.account.AccountDTO;
import com.pocketful.web.dto.account.NewAccountDTO;
import com.pocketful.web.mapper.AccountDTOMapper;
import com.pocketful.web.dto.account.AccountIdDTO;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("v1/accounts")
public class AccountController {
    private final AccountService accountService;
    private final AccountDTOMapper accountDTOMapper;

    @GetMapping
    public ResponseEntity<List<AccountDTO>> getAll() {
        List<AccountDTO> accounts = accountService.findAll().stream()
                .map(accountDTOMapper)
                .toList();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(accounts);
    }

    @PostMapping
    public ResponseEntity<AccountIdDTO> create(@RequestBody NewAccountDTO newAccountDTO) {
        Account account = accountService.create(newAccountDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new AccountIdDTO(account.getId()));
    }
}
