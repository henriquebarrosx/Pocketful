package com.pocketful.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pocketful.config.SecurityConfig;
import com.pocketful.controller.PaymentController;
import com.pocketful.entity.Account;
import com.pocketful.enums.PaymentSelectionOption;
import com.pocketful.service.AccountService;
import com.pocketful.service.PaymentService;
import com.pocketful.utils.*;
import com.pocketful.web.dto.payment.PaymentCreationRequestDTO;
import com.pocketful.web.dto.payment.PaymentEditionRequestDTO;
import com.pocketful.web.mapper.PaymentDTOMapper;
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

@Import(SecurityConfig.class)
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

    @Test
    public void shouldReturnPaymentsFromSignedAccountWhenGettingPaymentsBeenSignedIn() throws Exception {
        Account account = AccountBuilder.build();
        String token = SessionBuilder.build(account);

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
    public void shouldReturnPaymentsFromSignedAccountWhenCreatingPaymentBeenSignedIn() throws Exception {
        Account account = AccountBuilder.build();
        String token = SessionBuilder.build(account);
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

    @Test
    public void shouldReturnPaymentsFromSignedAccountWhenUpdatingPaymentBeenSignedIn() throws Exception {
        Account account = AccountBuilder.build();
        String token = SessionBuilder.build(account);
        var request = PaymentEditionRequestBuilder.build();

        Mockito.when(accountService.findByEmail(ArgumentMatchers.anyString()))
                .thenReturn(account);

        Mockito.doNothing().when(paymentService)
                .update(
                        ArgumentMatchers.any(Account.class),
                        ArgumentMatchers.anyLong(),
                        ArgumentMatchers.any(PaymentEditionRequestDTO.class)
                );

        mockmvc.perform(MockMvcRequestBuilders.put("/v1/payments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, token)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void shouldReturnPaymentsFromSignedAccountWhenDeletingPaymentBeenSignedIn() throws Exception {
        Account account = AccountBuilder.build();
        String token = SessionBuilder.build(account);
        var request = PaymentCreationRequestBuilder.build();

        Mockito.when(accountService.findByEmail(ArgumentMatchers.anyString()))
                .thenReturn(account);

        Mockito.doNothing().when(paymentService)
                .delete(
                        ArgumentMatchers.any(Account.class),
                        ArgumentMatchers.anyLong(),
                        ArgumentMatchers.any()
                );

        mockmvc.perform(MockMvcRequestBuilders.delete(String.format("/v1/payments/1?type=%s", PaymentSelectionOption.THIS_PAYMENT))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, token)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}
