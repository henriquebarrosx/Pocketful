package com.pocketful.unit.controller;

import com.pocketful.config.SecurityConfig;
import com.pocketful.controller.PaymentController;
import com.pocketful.entity.Account;
import com.pocketful.entity.Payment;
import com.pocketful.entity.PaymentCategory;
import com.pocketful.entity.PaymentFrequency;
import com.pocketful.enums.PaymentSelectionOption;
import com.pocketful.exception.Payment.PaymentNotFoundException;
import com.pocketful.mapper.PaymentCategoryMapper;
import com.pocketful.service.AccountService;
import com.pocketful.service.PaymentService;
import com.pocketful.util.JsonWebToken;
import com.pocketful.utils.AccountBuilder;
import com.pocketful.utils.PaymentBuilder;
import com.pocketful.utils.PaymentCategoryBuilder;
import com.pocketful.utils.PaymentCreationRequestBuilder;
import com.pocketful.utils.PaymentEditionRequestBuilder;
import com.pocketful.utils.PaymentFrequencyBuilder;
import com.pocketful.utils.SessionBuilder;
import com.pocketful.model.dto.payment.PaymentCreationRequestDTO;
import com.pocketful.model.dto.payment.PaymentDeletionRequestDTO;
import com.pocketful.model.dto.payment.PaymentEditionRequestDTO;
import com.pocketful.mapper.PaymentMapper;

import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static com.pocketful.config.SecurityFilterConfig.AUTHORIZATION;

@Import({SecurityConfig.class, JsonWebToken.class, PaymentMapper.class, PaymentCategoryMapper.class})
@WebMvcTest(PaymentController.class)
public class PaymentControllerTest {

    @Autowired
    private MockMvc mockmvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PaymentService paymentService;

    @MockBean
    private AccountService accountService;

    @Test
    public void shouldRespondWith200AndCallServiceWithoutStartAndEndAtDateWhenFilteringPayment() throws Exception {
        Account account = AccountBuilder.build();
        String token = SessionBuilder.build(account);

        Mockito.when(accountService.findByEmail(ArgumentMatchers.anyString()))
                .thenReturn(account);

        Mockito.when(paymentService.findBy(ArgumentMatchers.any(Account.class), ArgumentMatchers.any(LocalDate.class), ArgumentMatchers.any(LocalDate.class)))
                .thenReturn(Collections.emptyList());

        mockmvc.perform(MockMvcRequestBuilders.get("/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(0));

        Mockito.verify(paymentService, Mockito.times(1))
                .findBy(account, null, null);
    }

    @Test
    public void shouldRespondWith200AndCallServiceWithStartAndEndAtDateWhenFilteringPayment() throws Exception {
        Account account = AccountBuilder.build();
        String token = SessionBuilder.build(account);

        LocalDate startAt = LocalDate.of(2024, 10, 1);
        LocalDate endAt = LocalDate.of(2024, 10, 1);

        Mockito.when(accountService.findByEmail(ArgumentMatchers.anyString()))
                .thenReturn(account);

        Mockito.when(paymentService.findBy(ArgumentMatchers.any(Account.class), ArgumentMatchers.any(LocalDate.class), ArgumentMatchers.any(LocalDate.class)))
                .thenReturn(Collections.emptyList());

        mockmvc.perform(MockMvcRequestBuilders.get(String.format("/v1/payments?startAt=%s&endAt=%s", startAt, endAt))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(0));

        Mockito.verify(paymentService, Mockito.times(1))
                .findBy(account, startAt, endAt);
    }

    @Test
    public void shouldRespondWith200AndReturnPaymentsDTOInsteadPaymentEntityWhenFilteringPayment() throws Exception {
        Account account = AccountBuilder.build();
        PaymentCategory category = PaymentCategoryBuilder.build();
        PaymentFrequency frequency = PaymentFrequencyBuilder.build();

        String token = SessionBuilder.build(account);
        List<Payment> result = List.of(PaymentBuilder.build(account, category, frequency));

        LocalDate startAt = LocalDate.of(2024, 10, 1);
        LocalDate endAt = LocalDate.of(2024, 10, 1);

        Mockito.when(accountService.findByEmail(ArgumentMatchers.anyString()))
                .thenReturn(account);

        Mockito.when(paymentService.findBy(ArgumentMatchers.any(Account.class), ArgumentMatchers.any(LocalDate.class), ArgumentMatchers.any(LocalDate.class)))
                .thenReturn(result);

        mockmvc.perform(MockMvcRequestBuilders.get(String.format("/v1/payments?startAt=%s&endAt=%s", startAt, endAt))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].amount").value(BigDecimal.valueOf(1000)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value("Exame médico"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].payed").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].expense").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].frequencyTimes").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].deadlineAt").value(LocalDate.of(2024, 8, 5).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].paymentCategory.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].paymentCategory.name").value("Transport"));
    }

    @Test
    public void shouldRespondWith404WhenGettingNonExistentPayment() throws Exception {
        Account account = AccountBuilder.build();
        String token = SessionBuilder.build(account);

        Mockito.when(accountService.findByEmail(ArgumentMatchers.anyString()))
                .thenReturn(account);

        Mockito.doThrow(PaymentNotFoundException.class)
                .when(paymentService)
                .findById(ArgumentMatchers.any(Long.class), ArgumentMatchers.any(Account.class));

        mockmvc.perform(MockMvcRequestBuilders.get("/v1/payments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, token))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void shouldRespondWith200WhenGettingExistentPayment() throws Exception {
        Account account = AccountBuilder.build();
        PaymentCategory category = PaymentCategoryBuilder.build();
        PaymentFrequency frequency = PaymentFrequencyBuilder.build();
        Payment payment = PaymentBuilder.build(account, category, frequency);

        String token = SessionBuilder.build(account);

        Mockito.when(accountService.findByEmail(ArgumentMatchers.anyString()))
                .thenReturn(account);

        Mockito.when(paymentService.findById(ArgumentMatchers.any(Long.class), ArgumentMatchers.any(Account.class)))
                .thenReturn(payment);

        mockmvc.perform(MockMvcRequestBuilders.get("/v1/payments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.amount").value(BigDecimal.valueOf(1000)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Exame médico"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.payed").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.expense").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.frequencyTimes").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.deadlineAt").value(LocalDate.of(2024, 8, 5).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.paymentCategory.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.paymentCategory.name").value("Transport"));
    }

    @Test
    public void shouldRespondWith201AndReturnPaymentIdCreatingPayment() throws Exception {
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
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1));
    }

    @Test
    public void shouldOnlyRespondWith204WhenUpdatingPayment() throws Exception {
        Account account = AccountBuilder.build();
        String token = SessionBuilder.build(account);
        var request = PaymentEditionRequestBuilder.build();

        Mockito.when(accountService.findByEmail(ArgumentMatchers.anyString()))
                .thenReturn(account);

        Mockito.doNothing()
                .when(paymentService)
                .update(ArgumentMatchers.any(Account.class), ArgumentMatchers.anyLong(), ArgumentMatchers.any(PaymentEditionRequestDTO.class));

        mockmvc.perform(MockMvcRequestBuilders.put("/v1/payments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, token)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void shouldRespondWith204WhenDeletingPayment() throws Exception {
        Account account = AccountBuilder.build();
        String token = SessionBuilder.build(account);
        PaymentDeletionRequestDTO request = new PaymentDeletionRequestDTO(PaymentSelectionOption.THIS_PAYMENT);

        Mockito.when(accountService.findByEmail(ArgumentMatchers.anyString()))
                .thenReturn(account);

        Mockito.doNothing()
                .when(paymentService)
                .delete(ArgumentMatchers.any(Account.class), ArgumentMatchers.anyLong(), ArgumentMatchers.any());

        mockmvc.perform(MockMvcRequestBuilders.delete("/v1/payments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, token)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}
