package com.pocketful.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pocketful.config.SecurityConfig;
import com.pocketful.controller.AuthController;
import com.pocketful.service.AccountService;
import com.pocketful.service.AuthenticationService;
import com.pocketful.utils.AccountBuilder;
import com.pocketful.utils.SignInResponseBuilder;
import com.pocketful.utils.SignUpRequestBuilder;
import com.pocketful.model.dto.account.SignInRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)
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

    @BeforeEach
    public void setup() {
        objectMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();
    }

    @Test
    public void shouldReturnAnAccountWhenSignUp() throws Exception {
        var request = SignUpRequestBuilder.build();
        var account = AccountBuilder.build();

        when(accountService.create(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
                .thenReturn(account);

        this.mockMvc.perform(post("/v1/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(account.getId().intValue())));
    }

    @Test
    public void shouldReturnAnAuthenticatedAccountWhenSigningInUsingValidCredentials() throws Exception {
        var request = new SignInRequestDTO("john.doe@mail.com", "12345678");
        var authenticatedAccount = SignInResponseBuilder.build();

        when(authenticationService.authenticate(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(authenticatedAccount);

        this.mockMvc.perform(post("/v1/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(authenticatedAccount.getId().intValue())))
                .andExpect(jsonPath("$.name", is(authenticatedAccount.getName())))
                .andExpect(jsonPath("$.email", is(authenticatedAccount.getEmail())))
                .andExpect(jsonPath("$.role", is(authenticatedAccount.getRole().name())))
                .andExpect(jsonPath("$.token", is(authenticatedAccount.getToken())));
    }

}
