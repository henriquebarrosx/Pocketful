package com.pocketful.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pocketful.config.SpringSecurityConfig;
import com.pocketful.controller.PaymentController;
import com.pocketful.entity.Account;
import com.pocketful.service.AccountService;
import com.pocketful.service.PaymentService;
import com.pocketful.util.JsonWebToken;
import com.pocketful.utils.AccountBuilder;
import com.pocketful.utils.PaymentBuilder;
import com.pocketful.utils.PaymentCreationRequestBuilder;
import com.pocketful.web.dto.payment.PaymentCreationRequestDTO;
import com.pocketful.web.mapper.PaymentDTOMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.Collections;

import static com.pocketful.config.SecurityFilterConfig.AUTHORIZATION;

@Import(SpringSecurityConfig.class)
@WebMvcTest(PaymentController.class)
public class PaymentControllerTest {

    @Autowired
    private MockMvc mockmvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PaymentService paymentService;

    @MockBean
    private PaymentDTOMapper paymentDTOMapper;

    @MockBean
    private AccountService accountService;

    private String token;

    private Account account;

    @BeforeEach
    void beforeEach() {
        account = AccountBuilder.build();
        token = JsonWebToken.generate(account.getEmail());
    }

    @AfterEach
    void afterEach() {
        JsonWebToken.invalidate(account.getEmail());
    }

    @Test
    public void shouldThrowExceptionWhenGettingPaymentsWithoutBeenSignedIn() throws Exception {
        mockmvc.perform(MockMvcRequestBuilders.get("/v1/payments")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void shouldReturnPaymentsFromSignedAccountWhenGettingPaymentsBeenSignedIn() throws Exception {
        Mockito.when(accountService.findByEmail(ArgumentMatchers.anyString()))
            .thenReturn(account);

        Mockito.when(paymentService.findBy(ArgumentMatchers.any(Account.class), ArgumentMatchers.any(LocalDate.class), ArgumentMatchers.any(LocalDate.class)))
                .thenReturn(Collections.emptyList());

        mockmvc.perform(MockMvcRequestBuilders.get("/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, token))
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void shouldThrowExceptionWhenCreatingPaymentWithoutBeenSignedIn() throws Exception {
        var request = PaymentCreationRequestBuilder.build();

        mockmvc.perform(MockMvcRequestBuilders.post("/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void shouldReturnPaymentsFromSignedAccountWhenCreatingPaymentBeenSignedIn() throws Exception {
        var request = PaymentCreationRequestBuilder.build();

        Mockito.when(accountService.findByEmail(ArgumentMatchers.anyString()))
            .thenReturn(account);

        Mockito.when(paymentService.create(ArgumentMatchers.any(Account.class), ArgumentMatchers.any(PaymentCreationRequestDTO.class)))
            .thenReturn(PaymentBuilder.build());

        mockmvc.perform(MockMvcRequestBuilders.post("/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, token)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(MockMvcResultMatchers.status().isCreated());
    }
}
