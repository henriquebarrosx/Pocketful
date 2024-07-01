package com.pocketful.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pocketful.entity.Account;
import com.pocketful.entity.Payment;
import com.pocketful.entity.PaymentCategory;
import com.pocketful.entity.PaymentFrequency;
import com.pocketful.enums.PaymentDeletionOption;
import com.pocketful.producer.PaymentQueueProducer;
import com.pocketful.repository.AccountRepository;
import com.pocketful.repository.PaymentCategoryRepository;
import com.pocketful.repository.PaymentFrequencyRepository;
import com.pocketful.repository.PaymentRepository;
import com.pocketful.utils.AccountBuilder;
import com.pocketful.utils.PaymentBuilder;
import com.pocketful.utils.PaymentCategoryBuilder;
import com.pocketful.utils.PaymentFrequencyBuilder;
import com.pocketful.web.dto.payment.NewPaymentDTO;
import com.pocketful.web.dto.payment.PaymentDeleteDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
class PaymentControllerTest {
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentCategoryRepository paymentCategoryRepository;

    @Autowired
    private PaymentFrequencyRepository paymentFrequencyRepository;

    @Autowired
    private AccountRepository accountRepository;

    @MockBean
    private PaymentQueueProducer paymentQueueProducer;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    PaymentControllerTest() {
    }

    @BeforeEach
    public void setup() {
        objectMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();

        paymentRepository.deleteAll();
        paymentFrequencyRepository.deleteAll();
        paymentCategoryRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve retornar 404 ao cadastrar pagamento com conta inexistente")
    public void t1() throws Exception {
        NewPaymentDTO request = PaymentBuilder.buildNewPaymentRequest();

        mockMvc.perform(post("/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Account id do not exist.")));
    }

    @Test
    @DisplayName("Deve retornar 404 ao cadastrar pagamento com categoria inexistente")
    public void t2() throws Exception {
        Account account = accountRepository.save(AccountBuilder.buildAccount());
        NewPaymentDTO request = PaymentBuilder.buildNewPaymentRequest(account);

        mockMvc.perform(post("/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Payment Category not found")));
    }

    @Test
    @DisplayName("Deve retornar 409 ao cadastrar pagamento com valor menor ou igual a 0")
    public void t3() throws Exception {
        float amount = -10;
        Account account = accountRepository.save(AccountBuilder.buildAccount());
        PaymentCategory category = paymentCategoryRepository.save(PaymentCategoryBuilder.buildPaymentCategory());
        NewPaymentDTO request = PaymentBuilder.buildNewPaymentRequest(account, category, amount);

        mockMvc.perform(post("/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Amount should be greater than 0.")));
    }

    @Test
    @DisplayName("Deve retornar 201 ao cadastrar pagamento com conta e categoria existente, e um valor maior que 0")
    public void t4() throws Exception {
        float amount = 10;
        Account account = accountRepository.save(AccountBuilder.buildAccount());
        PaymentCategory category = paymentCategoryRepository.save(PaymentCategoryBuilder.buildPaymentCategory());
        NewPaymentDTO request = PaymentBuilder.buildNewPaymentRequest(account, category, amount);

        doNothing().when(paymentQueueProducer).processPaymentGeneration(any());

        mockMvc.perform(post("/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());

        assertEquals(1, paymentRepository.count());
    }

    @Test
    @DisplayName("Deve retornar 201 e o id do pagamento, bem como encaminhar o mesmo para uma fila contendo 5 frequências")
    public void t5() throws Exception {
        int frequencyTimes = 5;
        Account account = accountRepository.save(AccountBuilder.buildAccount());
        PaymentCategory category = paymentCategoryRepository.save(PaymentCategoryBuilder.buildPaymentCategory());
        NewPaymentDTO request = PaymentBuilder.buildNewPaymentRequest(account, category, frequencyTimes);

        ArgumentCaptor<Payment> paymentArgumentCaptor = ArgumentCaptor.forClass(Payment.class);
        doNothing().when(paymentQueueProducer).processPaymentGeneration(paymentArgumentCaptor.capture());

        mockMvc.perform(post("/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());

        assertEquals(1, paymentRepository.count());
        verify(paymentQueueProducer, times(1)).processPaymentGeneration(any());
        assertEquals(account.getId(), paymentArgumentCaptor.getValue().getAccount().getId());
        assertEquals(category.getId(), paymentArgumentCaptor.getValue().getPaymentCategory().getId());
        assertEquals(frequencyTimes, paymentArgumentCaptor.getValue().getPaymentFrequency().getTimes());
    }

    @Test
    @DisplayName("Deve retornar 400 ao cadastra pagamentos com frequência superior a 600")
    public void t6() throws Exception {
        int frequencyTimes = 800;
        Account account = accountRepository.save(AccountBuilder.buildAccount());
        PaymentCategory category = paymentCategoryRepository.save(PaymentCategoryBuilder.buildPaymentCategory());
        NewPaymentDTO request = PaymentBuilder.buildNewPaymentRequest(account, category, frequencyTimes);

        mockMvc.perform(post("/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Invalid frequency times")));

        assertEquals(0, paymentRepository.count());
        assertEquals(0, paymentFrequencyRepository.count());
    }

    @Test
    @DisplayName("Deve retornar 201 e o id do pagamento, bem como encaminhar o mesmo para uma fila contendo 600 frequências quando indeterminada")
    public void t7() throws Exception {
        boolean isIndeterminate = true;
        Account account = accountRepository.save(AccountBuilder.buildAccount());
        PaymentCategory category = paymentCategoryRepository.save(PaymentCategoryBuilder.buildPaymentCategory());
        NewPaymentDTO request = PaymentBuilder.buildNewPaymentRequest(account, category, isIndeterminate);

        ArgumentCaptor<Payment> paymentArgumentCaptor = ArgumentCaptor.forClass(Payment.class);
        doNothing().when(paymentQueueProducer).processPaymentGeneration(paymentArgumentCaptor.capture());

        mockMvc.perform(post("/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());

        assertEquals(1, paymentFrequencyRepository.count());
        verify(paymentQueueProducer, times(1)).processPaymentGeneration(any());
        assertEquals(account.getId(), paymentArgumentCaptor.getValue().getAccount().getId());
        assertEquals(category.getId(), paymentArgumentCaptor.getValue().getPaymentCategory().getId());
        assertEquals(600, paymentArgumentCaptor.getValue().getPaymentFrequency().getTimes());
    }

    @Test
    @DisplayName("Deve retornar 200 e buscar por todos pagamentos")
    public void t8() throws Exception {
        Account account = accountRepository.save(PaymentBuilder.buildPayment().getAccount());
        PaymentCategory category = paymentCategoryRepository.save(PaymentBuilder.buildPayment().getPaymentCategory());
        PaymentFrequency paymentFrequency = paymentFrequencyRepository.save(PaymentBuilder.buildPayment().getPaymentFrequency());
        Payment payment = paymentRepository.save(PaymentBuilder.buildPayment(account, category, paymentFrequency));

        mockMvc.perform(get("/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(payment.getId().intValue())))
                .andExpect(jsonPath("$[0].amount", is(10.0)))
                .andExpect(jsonPath("$[0].description", is("Uber")))
                .andExpect(jsonPath("$[0].payed", is(false)))
                .andExpect(jsonPath("$[0].expense", is(true)))
                .andExpect(jsonPath("$[0].frequencyTimes", is(paymentFrequency.getTimes())))
                .andExpect(jsonPath("$[0].deadlineAt", is("2024-10-01")))
                .andExpect(jsonPath("$[0].paymentCategory.id", is(category.getId().intValue())))
                .andExpect(jsonPath("$[0].paymentCategory.name", is("Saúde")));
    }

    @Test
    @DisplayName("Deve retornar 200 e buscar por todos pagamentos entre duas datas")
    public void t9() throws Exception {
        Account account = accountRepository.save(PaymentBuilder.buildPayment().getAccount());
        PaymentCategory category = paymentCategoryRepository.save(PaymentBuilder.buildPayment().getPaymentCategory());
        PaymentFrequency paymentFrequency = paymentFrequencyRepository.save(PaymentBuilder.buildPayment().getPaymentFrequency());

        paymentRepository.saveAll(
                List.of(
                        PaymentBuilder.buildPayment(account, category, paymentFrequency, "2024-05-01"),
                        PaymentBuilder.buildPayment(account, category, paymentFrequency, "2024-06-05"),
                        PaymentBuilder.buildPayment(account, category, paymentFrequency, "2024-06-10"),
                        PaymentBuilder.buildPayment(account, category, paymentFrequency, "2024-07-15")
                )
        );

        String startDate = "2024-06-01";
        String endDate = "2024-06-30";

        mockMvc.perform(get(String.format("/v1/payments?startAt=%s&endAt=%s", startDate, endDate))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("Deve retornar 404 ao tentar excluir pagamento inexistente")
    public void t10() throws Exception {
        mockMvc.perform(delete(String.format("/v1/payments/%s", 1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PaymentDeleteDTO(PaymentDeletionOption.THIS_PAYMENT))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Payment not found")));
    }

    @Test
    @DisplayName("Deve retornar 204 ao excluir apenas o pagamento com o id informado")
    public void t11() throws Exception {
        Account account = accountRepository.save(PaymentBuilder.buildPayment().getAccount());
        PaymentCategory category = paymentCategoryRepository.save(PaymentBuilder.buildPayment().getPaymentCategory());
        PaymentFrequency paymentFrequency = paymentFrequencyRepository.save(PaymentFrequencyBuilder.buildPaymentFrequency(1));
        Payment payment = paymentRepository.save(PaymentBuilder.buildPayment(account, category, paymentFrequency));

        mockMvc.perform(delete(String.format("/v1/payments/%s", payment.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PaymentDeleteDTO(PaymentDeletionOption.THIS_PAYMENT))))
                .andExpect(status().isNoContent());

        assertEquals(0, paymentRepository.count());
        assertEquals(0, paymentFrequencyRepository.count());
    }

    @Test
    @DisplayName("Deve retornar 204 e excluir apenas pagamentos com data de vencimento igual e superior ao do pagamento informado")
    public void t12() throws Exception {
        Account account = accountRepository.save(PaymentBuilder.buildPayment().getAccount());
        PaymentCategory category = paymentCategoryRepository.save(PaymentBuilder.buildPayment().getPaymentCategory());
        PaymentFrequency paymentFrequency = paymentFrequencyRepository.save(PaymentFrequencyBuilder.buildPaymentFrequency(4));

        paymentRepository.save(PaymentBuilder.buildPayment(account, category, paymentFrequency, "2024-05-01"));
        paymentRepository.save(PaymentBuilder.buildPayment(account, category, paymentFrequency, "2024-05-05"));
        Payment p3 = paymentRepository.save(PaymentBuilder.buildPayment(account, category, paymentFrequency, "2024-06-10"));
        paymentRepository.save(PaymentBuilder.buildPayment(account, category, paymentFrequency, "2024-06-15"));
        paymentRepository.save(PaymentBuilder.buildPayment(account, category, paymentFrequency, "2024-07-20"));

        mockMvc.perform(delete(String.format("/v1/payments/%s", p3.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PaymentDeleteDTO(PaymentDeletionOption.THIS_AND_FUTURE_PAYMENTS))))
                .andExpect(status().isNoContent());

        assertEquals(2, paymentRepository.count());
        assertEquals(1, paymentFrequencyRepository.count());
    }

    @Test
    @DisplayName("Deve retornar 204 e excluir todos os pagamentos baseado na frequencia do pagamento informado")
    public void t13() throws Exception {
        Account account = accountRepository.save(PaymentBuilder.buildPayment().getAccount());
        PaymentCategory category = paymentCategoryRepository.save(PaymentBuilder.buildPayment().getPaymentCategory());
        PaymentFrequency paymentFrequency = paymentFrequencyRepository.save(PaymentFrequencyBuilder.buildPaymentFrequency(4));

        paymentRepository.save(PaymentBuilder.buildPayment(account, category, paymentFrequency, "2024-05-01"));
        paymentRepository.save(PaymentBuilder.buildPayment(account, category, paymentFrequency, "2024-05-05"));
        Payment p3 = paymentRepository.save(PaymentBuilder.buildPayment(account, category, paymentFrequency, "2024-06-10"));
        paymentRepository.save(PaymentBuilder.buildPayment(account, category, paymentFrequency, "2024-06-15"));
        paymentRepository.save(PaymentBuilder.buildPayment(account, category, paymentFrequency, "2024-07-20"));

        mockMvc.perform(delete(String.format("/v1/payments/%s", p3.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PaymentDeleteDTO(PaymentDeletionOption.ALL_PAYMENTS))))
                .andExpect(status().isNoContent());

        assertEquals(0, paymentRepository.count());
        assertEquals(0, paymentFrequencyRepository.count());
    }

    @Test
    @DisplayName("Deve retornar 400 ao tentar excluir um pagamento com tipo inexistente")
    public void t14() throws Exception {
        Account account = accountRepository.save(PaymentBuilder.buildPayment().getAccount());
        PaymentCategory category = paymentCategoryRepository.save(PaymentBuilder.buildPayment().getPaymentCategory());
        PaymentFrequency paymentFrequency = paymentFrequencyRepository.save(PaymentFrequencyBuilder.buildPaymentFrequency(4));

        Payment payment = paymentRepository.save(PaymentBuilder.buildPayment(account, category, paymentFrequency, "2024-05-01"));

        mockMvc.perform(delete(String.format("/v1/payments/%s", payment.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\": \"LOREM_IPSUM\"}"))
                .andExpect(status().isBadRequest());

        assertEquals(1, paymentRepository.count());
        assertEquals(1, paymentFrequencyRepository.count());
    }
}