package com.pocketful.controller;

import com.pocketful.web.dto.AccountDTO;
import com.pocketful.service.AccountService;

import com.pocketful.web.mapper.AccountDTOMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
