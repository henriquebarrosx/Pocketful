package com.pocketful.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pocketful.config.SpringSecurityConfig;
import com.pocketful.exception.EmailOrPhoneNumberAlreadyExistException;
import com.pocketful.exception.InvalidCredentialsException;
import com.pocketful.exception.InvalidPhoneNumberException;
import com.pocketful.service.AccountService;
import com.pocketful.service.AuthenticationService;
import com.pocketful.service.TokenService;
import com.pocketful.utils.AccountBuilder;
import com.pocketful.utils.AuthenticatedAccountBuilder;
import com.pocketful.utils.NewAccountRequestBuilder;
import com.pocketful.web.dto.account.NewAccountRequestDTO;
import com.pocketful.web.dto.account.SignInRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SpringSecurityConfig.class)
@ExtendWith(SpringExtension.class)
@WebMvcTest(AuthController.class)
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

    String token = "TOKEN_FOR_TEST_ONLY";

    @BeforeEach
    public void setup() {
        objectMapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();
    }

    @Test
    public void shouldThrowExceptionWhenProvideBadCredentials() throws Exception {
        var request = new SignInRequestDTO("john.doe@mail.com", "12345678");

        when(authenticationService.authenticate(ArgumentMatchers.any(SignInRequestDTO.class)))
            .thenThrow(new InvalidCredentialsException());

        this.mockMvc
            .perform(MockMvcRequestBuilders.post("/v1/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isForbidden());
    }

    @Test
    public void shouldThrowExceptionWhenDontProvidePassword() throws Exception {
        var request = "{\"email\":\"john.doe@mail.com\"}";

        this.mockMvc
            .perform(MockMvcRequestBuilders.post("/v1/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnAccountDetailsAndTokenWhenProvideValidCredentials() throws Exception {
        var request = new SignInRequestDTO("john.doe@mail.com", "12345678");
        var response = AuthenticatedAccountBuilder.build();

        when(authenticationService.authenticate(Mockito.any(SignInRequestDTO.class)))
            .thenReturn(response);

        this.mockMvc
            .perform(MockMvcRequestBuilders.post("/v1/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(response.getId().intValue())))
            .andExpect(jsonPath("$.name", is(response.getName())))
            .andExpect(jsonPath("$.email", is(response.getEmail())))
            .andExpect(jsonPath("$.role", is(response.getRole().name())))
            .andExpect(jsonPath("$.token", is(response.getToken())));
    }

    @Test
    public void shouldThrowExceptionWhenCreatingAccountUsingExistentEmailOrPhoneNumber() throws Exception {
        var request = NewAccountRequestBuilder.build();

        doThrow(new EmailOrPhoneNumberAlreadyExistException(request.getEmail(), request.getPhoneNumber()))
            .when(accountService).create(ArgumentMatchers.any(NewAccountRequestDTO.class));

        this.mockMvc
            .perform(post("/v1/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.message", is(String.format("Account using email %s or phone number %s already exists.", request.getEmail(), request.getPhoneNumber()))));
    }

    @Test
    public void shouldThrowExceptionWhenCreatingAccountUsingBadFormattedPhoneNumber() throws Exception {
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
    public void shouldReturnAccountIdWhenCreatingNewOneSuccessfully() throws Exception {
        var request = NewAccountRequestBuilder.build();

        when(accountService.create(ArgumentMatchers.any(NewAccountRequestDTO.class)))
            .thenReturn(AccountBuilder.build());

        this.mockMvc
            .perform(post("/v1/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    public void shouldReturnNoContentWhenFinishingSessionUsingValidToken() throws Exception {
        when(tokenService.validateToken(ArgumentMatchers.any(), ArgumentMatchers.any()))
            .thenReturn(true);

        when(tokenService.decodeToken(ArgumentMatchers.any()))
            .thenReturn(AccountBuilder.build());

        this.mockMvc
            .perform(delete("/v1/auth/sign-out")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isNoContent());
    }

    @Test
    public void shouldThrowExceptionWhenFinishingSessionUsingInvalidToken() throws Exception {
        when(tokenService.validateToken(ArgumentMatchers.any(), ArgumentMatchers.any()))
            .thenReturn(false);

        when(tokenService.decodeToken(ArgumentMatchers.any()))
            .thenReturn(AccountBuilder.build());

        this.mockMvc
            .perform(delete("/v1/auth/sign-out")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isForbidden());
    }

    @Test
    public void shouldThrowExceptionWhenFinishingSessionUsingBadFormattedToken() throws Exception {
        this.mockMvc
            .perform(delete("/v1/auth/sign-out")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isForbidden());
    }
}
