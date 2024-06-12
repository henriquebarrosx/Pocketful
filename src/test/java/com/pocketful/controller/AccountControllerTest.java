package com.pocketful.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.pocketful.entity.Account;
import com.pocketful.utils.AccountBuilder;
import com.pocketful.repository.AccountRepository;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static com.pocketful.ConcurrentTestHelper.doSyncAndConcurrently;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AccountControllerTest {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        objectMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();

        accountRepository.deleteAll();
    }

    @Test
    @DisplayName("deve retornar status 200 e id de conta cadastrada ao cadastrar conta com email e número de telefone inexistentes")
    public void t1() throws Exception {
        this.mockMvc.perform(post("/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(AccountBuilder.buildNewAccountRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());

        assertEquals(1, accountRepository.count());
    }

    @Test
    @DisplayName("não deve cadastrar nova conta com e-mail e número telefone repetido em alta-concorrência")
    public void t2() {
        doSyncAndConcurrently(3,
                () -> this.mockMvc.perform(post("/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(AccountBuilder.buildNewAccountRequest())))
        );

        assertEquals(1, accountRepository.count());
    }

    @Test
    @DisplayName("deve retornar status 409 ao cadastrar conta com email e número de telefone já existentes")
    public void t3() throws Exception {
        accountRepository.save(AccountBuilder.buildAccount());

        this.mockMvc.perform(post("/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(AccountBuilder.buildNewAccountRequest())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("An account with this email or phone number already exists."));

        assertEquals(1, accountRepository.count());
    }

    @Test
    @DisplayName("deve retornar status 200 e lista contendo N usuários")
    public void t4() throws Exception {
        Account account = accountRepository.save(AccountBuilder.buildAccount());

        this.mockMvc.perform(get("/v1/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(account.getId().intValue())))
                .andExpect(jsonPath("$[0].name", is("John Doe")))
                .andExpect(jsonPath("$[0].email", is("john.doe@mail.com")))
                .andExpect(jsonPath("$[0].phoneNumber", is("5582988776655")));
    }

    @Test
    @DisplayName("deve retornar status 404 quando não encontrar conta com pelo ID")
    public void t5() throws Exception {
        this.mockMvc.perform(get("/v1/accounts/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Account id do not exist."));
    }

    @Test
    @DisplayName("deve retornar status 200 e 1 conta existente")
    public void t6() throws Exception {
        Account account = accountRepository.save(AccountBuilder.buildAccount());

        this.mockMvc.perform(get("/v1/accounts/" + account.getId().intValue()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(account.getId().intValue())))
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john.doe@mail.com")))
                .andExpect(jsonPath("$.phoneNumber", is("5582988776655")));
    }
}