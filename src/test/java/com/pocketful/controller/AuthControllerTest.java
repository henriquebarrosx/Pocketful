package com.pocketful.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pocketful.exception.Account.EmailOrPhoneNumberAlreadyExistException;
import com.pocketful.exception.Account.InvalidCredentialsException;
import com.pocketful.exception.Account.InvalidPhoneNumberException;
import com.pocketful.service.AccountService;
import com.pocketful.service.AuthenticationService;
import com.pocketful.service.TokenService;
import com.pocketful.utils.AccountBuilder;
import com.pocketful.utils.AuthenticatedAccountBuilder;
import com.pocketful.utils.NewAccountRequestBuilder;
import com.pocketful.web.dto.account.SignInRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private AccountService accountService;

    @MockBean
    private TokenService tokenService;

    @BeforeEach
    public void setup() {
        objectMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();
    }

    @Test
    public void authenticatingWithInvalidCredentials() throws Exception {
        var request = new SignInRequestDTO("john.doe@mail.com", "12345678");

        doThrow(new InvalidCredentialsException())
                .when(authenticationService).authenticate(ArgumentMatchers.any());

        this.mockMvc
                .perform(post("/v1/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void authenticatingWithValidCredentials() throws Exception {
        var request = new SignInRequestDTO("john.doe@mail.com", "12345678");
        var authenticatedAccount = AuthenticatedAccountBuilder.build();

        when(authenticationService.authenticate(ArgumentMatchers.any()))
                .thenReturn(authenticatedAccount);

        this.mockMvc
                .perform(post("/v1/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(authenticatedAccount.getId().intValue())))
                .andExpect(jsonPath("$.name", is(authenticatedAccount.getName())))
                .andExpect(jsonPath("$.email", is(authenticatedAccount.getEmail())))
                .andExpect(jsonPath("$.role", is(authenticatedAccount.getRole().name())))
                .andExpect(jsonPath("$.token", is(authenticatedAccount.getToken())));
    }

    @Test
    public void creatingAccountWithExistentEmailOrPhoneNumber() throws Exception {
        var request = NewAccountRequestBuilder.build();

        doThrow(new EmailOrPhoneNumberAlreadyExistException(request.getEmail(), request.getPhoneNumber()))
                .when(accountService).create(ArgumentMatchers.any());

        this.mockMvc
                .perform(post("/v1/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", is(String.format("Account using email %s or phone number %s already exists.", request.getEmail(), request.getPhoneNumber()))));
    }

    @Test
    public void creatingAccountWithInvalidPhoneNumber() throws Exception {
        var request = NewAccountRequestBuilder.build();

        doThrow(new InvalidPhoneNumberException())
                .when(accountService).create(ArgumentMatchers.any());

        this.mockMvc
                .perform(post("/v1/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Invalid account phone number. Please, provide valid country (Ex.: +55) and DDD number (Ex: 82).")));
    }

    @Test
    public void finishingSession() throws Exception {
        when(tokenService.decodeToken(ArgumentMatchers.any()))
                .thenReturn(AccountBuilder.build());

        this.mockMvc
                .perform(delete("/v1/auth/sign-out")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
