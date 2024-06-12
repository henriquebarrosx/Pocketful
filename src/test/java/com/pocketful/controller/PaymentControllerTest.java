package com.pocketful.controller;

import com.pocketful.entity.Account;
import com.pocketful.entity.Payment;
import com.pocketful.utils.AccountBuilder;
import com.pocketful.utils.PaymentBuilder;
import com.pocketful.entity.PaymentCategory;
import com.pocketful.entity.PaymentFrequency;
import com.pocketful.utils.PaymentCategoryBuilder;
import com.pocketful.repository.AccountRepository;
import com.pocketful.repository.PaymentRepository;
import com.pocketful.web.dto.payment.NewPaymentDTO;
import com.pocketful.repository.PaymentCategoryRepository;
import com.pocketful.repository.PaymentFrequencyRepository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        objectMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();

        paymentRepository.deleteAll();
        paymentCategoryRepository.deleteAll();
        paymentFrequencyRepository.deleteAll();
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

        mockMvc.perform(post("/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());

        assertEquals(1, paymentRepository.count());
    }

    @Test
    @DisplayName("Deve retornar 201 e gerar N outros pagamentos baseado na frequência do pagamento")
    public void t5() throws Exception {
        int frequencyTimes = 5;
        Account account = accountRepository.save(AccountBuilder.buildAccount());
        PaymentCategory category = paymentCategoryRepository.save(PaymentCategoryBuilder.buildPaymentCategory());
        NewPaymentDTO request = PaymentBuilder.buildNewPaymentRequest(account, category, frequencyTimes);

        mockMvc.perform(post("/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());

        assertEquals(5, paymentRepository.count());
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
    @DisplayName("Deve retornar 201 e cadastrar 600 pagamentos quando frequência for indeterminada")
    public void t7() throws Exception {
        boolean isIndeterminate = true;
        Account account = accountRepository.save(AccountBuilder.buildAccount());
        PaymentCategory category = paymentCategoryRepository.save(PaymentCategoryBuilder.buildPaymentCategory());
        NewPaymentDTO request = PaymentBuilder.buildNewPaymentRequest(account, category, isIndeterminate);

        mockMvc.perform(post("/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());

        assertEquals(600, paymentRepository.count());
        assertEquals(1, paymentFrequencyRepository.count());
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
}