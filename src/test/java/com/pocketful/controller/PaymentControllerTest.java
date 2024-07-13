package com.pocketful.controller;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
class PaymentControllerTest {
//    @Autowired
//    private PaymentRepository paymentRepository;
//
//    @Autowired
//    private PaymentCategoryRepository paymentCategoryRepository;
//
//    @Autowired
//    private PaymentFrequencyRepository paymentFrequencyRepository;
//
//    @Autowired
//    private AccountRepository accountRepository;
//
//    @MockBean
//    private PaymentGenerationQueueProducer paymentGenerationQueueProducer;
//
//    @MockBean
//    private PaymentEditionQueueProducer paymentEditionQueueProducer;
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    private ObjectMapper objectMapper;
//
//    PaymentControllerTest() {
//    }
//
//    @BeforeEach
//    public void setup() {
//        objectMapper = JsonMapper.builder()
//                .addModule(new JavaTimeModule())
//                .build();
//
//        paymentRepository.deleteAll();
//        paymentFrequencyRepository.deleteAll();
//        paymentCategoryRepository.deleteAll();
//        accountRepository.deleteAll();
//    }
//
//    @Test
//    @DisplayName("Deve retornar 404 ao cadastrar pagamento com conta inexistente")
//    public void t1() throws Exception {
//        NewPaymentDTO request = PaymentBuilder.buildNewPaymentRequest();
//
//        mockMvc.perform(post("/v1/payments")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.message", is("Account id do not exist.")));
//    }
//
//    @Test
//    @DisplayName("Deve retornar 404 ao cadastrar pagamento com categoria inexistente")
//    public void t2() throws Exception {
//        Account account = accountRepository.save(AccountBuilder.buildAccount());
//        NewPaymentDTO request = PaymentBuilder.buildNewPaymentRequest(account);
//
//        mockMvc.perform(post("/v1/payments")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.message", is("Payment Category not found")));
//    }
//
//    @Test
//    @DisplayName("Deve retornar 409 ao cadastrar pagamento com valor menor ou igual a 0")
//    public void t3() throws Exception {
//        float amount = -10;
//        Account account = accountRepository.save(AccountBuilder.buildAccount());
//        PaymentCategory category = paymentCategoryRepository.save(PaymentCategoryBuilder.buildPaymentCategory());
//        NewPaymentDTO request = PaymentBuilder.buildNewPaymentRequest(account, category, amount);
//
//        mockMvc.perform(post("/v1/payments")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.message", is("Amount should be greater or equals 0.")));
//    }
//
//    @Test
//    @DisplayName("Deve retornar 201 ao cadastrar pagamento com conta e categoria existente, e um valor maior que 0")
//    public void t4() throws Exception {
//        float amount = 10;
//        Account account = accountRepository.save(AccountBuilder.buildAccount());
//        PaymentCategory category = paymentCategoryRepository.save(PaymentCategoryBuilder.buildPaymentCategory());
//        NewPaymentDTO request = PaymentBuilder.buildNewPaymentRequest(account, category, amount);
//
//        doNothing().when(paymentGenerationQueueProducer).processPaymentGeneration(any());
//
//        mockMvc.perform(post("/v1/payments")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.id").exists());
//
//        assertEquals(1, paymentRepository.count());
//    }
//
//    @Test
//    @DisplayName("Deve retornar 201 e o id do pagamento, bem como encaminhar o mesmo para uma fila contendo 5 frequências")
//    public void t5() throws Exception {
//        int frequencyTimes = 5;
//        Account account = accountRepository.save(AccountBuilder.buildAccount());
//        PaymentCategory category = paymentCategoryRepository.save(PaymentCategoryBuilder.buildPaymentCategory());
//        NewPaymentDTO request = PaymentBuilder.buildNewPaymentRequest(account, category, frequencyTimes);
//
//        ArgumentCaptor<Payment> paymentArgumentCaptor = ArgumentCaptor.forClass(Payment.class);
//        doNothing().when(paymentGenerationQueueProducer).processPaymentGeneration(paymentArgumentCaptor.capture());
//
//        mockMvc.perform(post("/v1/payments")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.id").exists());
//
//        assertEquals(1, paymentRepository.count());
//        verify(paymentGenerationQueueProducer, times(1)).processPaymentGeneration(any());
//        assertEquals(account.getId(), paymentArgumentCaptor.getValue().getAccount().getId());
//        assertEquals(category.getId(), paymentArgumentCaptor.getValue().getPaymentCategory().getId());
//        assertEquals(frequencyTimes, paymentArgumentCaptor.getValue().getPaymentFrequency().getTimes());
//    }
//
//    @Test
//    @DisplayName("Deve retornar 400 ao cadastra pagamentos com frequência superior a 600")
//    public void t6() throws Exception {
//        int frequencyTimes = 800;
//        Account account = accountRepository.save(AccountBuilder.buildAccount());
//        PaymentCategory category = paymentCategoryRepository.save(PaymentCategoryBuilder.buildPaymentCategory());
//        NewPaymentDTO request = PaymentBuilder.buildNewPaymentRequest(account, category, frequencyTimes);
//
//        mockMvc.perform(post("/v1/payments")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.message", is("Invalid frequency times")));
//
//        assertEquals(0, paymentRepository.count());
//        assertEquals(0, paymentFrequencyRepository.count());
//    }
//
//    @Test
//    @DisplayName("Deve retornar 201 e o id do pagamento, bem como encaminhar o mesmo para uma fila contendo 600 frequências quando indeterminada")
//    public void t7() throws Exception {
//        boolean isIndeterminate = true;
//        Account account = accountRepository.save(AccountBuilder.buildAccount());
//        PaymentCategory category = paymentCategoryRepository.save(PaymentCategoryBuilder.buildPaymentCategory());
//        NewPaymentDTO request = PaymentBuilder.buildNewPaymentRequest(account, category, isIndeterminate);
//
//        ArgumentCaptor<Payment> paymentArgumentCaptor = ArgumentCaptor.forClass(Payment.class);
//        doNothing().when(paymentGenerationQueueProducer).processPaymentGeneration(paymentArgumentCaptor.capture());
//
//        mockMvc.perform(post("/v1/payments")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.id").exists());
//
//        assertEquals(1, paymentFrequencyRepository.count());
//        verify(paymentGenerationQueueProducer, times(1)).processPaymentGeneration(any());
//        assertEquals(account.getId(), paymentArgumentCaptor.getValue().getAccount().getId());
//        assertEquals(category.getId(), paymentArgumentCaptor.getValue().getPaymentCategory().getId());
//        assertEquals(600, paymentArgumentCaptor.getValue().getPaymentFrequency().getTimes());
//    }
//
//    @Test
//    @DisplayName("Deve retornar 200 e buscar por todos pagamentos")
//    public void t8() throws Exception {
//        Account account = accountRepository.save(PaymentBuilder.buildPayment().getAccount());
//        PaymentCategory category = paymentCategoryRepository.save(PaymentBuilder.buildPayment().getPaymentCategory());
//        PaymentFrequency paymentFrequency = paymentFrequencyRepository.save(PaymentBuilder.buildPayment().getPaymentFrequency());
//        Payment payment = paymentRepository.save(PaymentBuilder.buildPayment(account, category, paymentFrequency));
//
//        mockMvc.perform(get("/v1/payments")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(1)))
//                .andExpect(jsonPath("$[0].id", is(payment.getId().intValue())))
//                .andExpect(jsonPath("$[0].amount", is(10.0)))
//                .andExpect(jsonPath("$[0].description", is("Uber")))
//                .andExpect(jsonPath("$[0].payed", is(false)))
//                .andExpect(jsonPath("$[0].expense", is(true)))
//                .andExpect(jsonPath("$[0].frequencyTimes", is(paymentFrequency.getTimes())))
//                .andExpect(jsonPath("$[0].deadlineAt", is("2024-10-01")))
//                .andExpect(jsonPath("$[0].paymentCategory.id", is(category.getId().intValue())))
//                .andExpect(jsonPath("$[0].paymentCategory.name", is("Saúde")));
//    }
//
//    @Test
//    @DisplayName("Deve retornar 200 e buscar por todos pagamentos entre duas datas")
//    public void t9() throws Exception {
//        Account account = accountRepository.save(PaymentBuilder.buildPayment().getAccount());
//        PaymentCategory category = paymentCategoryRepository.save(PaymentBuilder.buildPayment().getPaymentCategory());
//        PaymentFrequency paymentFrequency = paymentFrequencyRepository.save(PaymentBuilder.buildPayment().getPaymentFrequency());
//
//        paymentRepository.saveAll(
//                List.of(
//                        PaymentBuilder.buildPayment(account, category, paymentFrequency, "2024-05-01"),
//                        PaymentBuilder.buildPayment(account, category, paymentFrequency, "2024-06-05"),
//                        PaymentBuilder.buildPayment(account, category, paymentFrequency, "2024-06-10"),
//                        PaymentBuilder.buildPayment(account, category, paymentFrequency, "2024-07-15")
//                )
//        );
//
//        String startDate = "2024-06-01";
//        String endDate = "2024-06-30";
//
//        mockMvc.perform(get(String.format("/v1/payments?startAt=%s&endAt=%s", startDate, endDate))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(2)));
//    }
//
//    @Test
//    @DisplayName("Deve retornar 404 ao tentar excluir pagamento inexistente")
//    public void t10() throws Exception {
//        mockMvc.perform(delete(String.format("/v1/payments/%s", 1))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(new PaymentDeleteDTO(PaymentSelectionOption.THIS_PAYMENT))))
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.message", is("Payment not found")));
//    }
//
//    @Test
//    @DisplayName("Deve retornar 204 ao excluir apenas o pagamento com o id informado")
//    public void t11() throws Exception {
//        Account account = accountRepository.save(PaymentBuilder.buildPayment().getAccount());
//        PaymentCategory category = paymentCategoryRepository.save(PaymentBuilder.buildPayment().getPaymentCategory());
//        PaymentFrequency paymentFrequency = paymentFrequencyRepository.save(PaymentFrequencyBuilder.buildPaymentFrequency(1));
//        Payment payment = paymentRepository.save(PaymentBuilder.buildPayment(account, category, paymentFrequency));
//
//        mockMvc.perform(delete(String.format("/v1/payments/%s", payment.getId()))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(new PaymentDeleteDTO(PaymentSelectionOption.THIS_PAYMENT))))
//                .andExpect(status().isNoContent());
//
//        assertEquals(0, paymentRepository.count());
//        assertEquals(0, paymentFrequencyRepository.count());
//    }
//
//    @Test
//    @DisplayName("Deve retornar 204 e excluir apenas pagamentos com data de vencimento igual e superior ao do pagamento informado")
//    public void t12() throws Exception {
//        Account account = accountRepository.save(PaymentBuilder.buildPayment().getAccount());
//        PaymentCategory category = paymentCategoryRepository.save(PaymentBuilder.buildPayment().getPaymentCategory());
//        PaymentFrequency paymentFrequency = paymentFrequencyRepository.save(PaymentFrequencyBuilder.buildPaymentFrequency(4));
//
//        paymentRepository.save(PaymentBuilder.buildPayment(account, category, paymentFrequency, "2024-05-01"));
//        paymentRepository.save(PaymentBuilder.buildPayment(account, category, paymentFrequency, "2024-05-05"));
//        Payment p3 = paymentRepository.save(PaymentBuilder.buildPayment(account, category, paymentFrequency, "2024-06-10"));
//        paymentRepository.save(PaymentBuilder.buildPayment(account, category, paymentFrequency, "2024-06-15"));
//        paymentRepository.save(PaymentBuilder.buildPayment(account, category, paymentFrequency, "2024-07-20"));
//
//        mockMvc.perform(delete(String.format("/v1/payments/%s", p3.getId()))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(new PaymentDeleteDTO(PaymentSelectionOption.THIS_AND_FUTURE_PAYMENTS))))
//                .andExpect(status().isNoContent());
//
//        assertEquals(2, paymentRepository.count());
//        assertEquals(1, paymentFrequencyRepository.count());
//    }
//
//    @Test
//    @DisplayName("Deve retornar 204 e excluir todos os pagamentos baseado na frequencia do pagamento informado")
//    public void t13() throws Exception {
//        Account account = accountRepository.save(PaymentBuilder.buildPayment().getAccount());
//        PaymentCategory category = paymentCategoryRepository.save(PaymentBuilder.buildPayment().getPaymentCategory());
//        PaymentFrequency paymentFrequency = paymentFrequencyRepository.save(PaymentFrequencyBuilder.buildPaymentFrequency(4));
//
//        paymentRepository.save(PaymentBuilder.buildPayment(account, category, paymentFrequency, "2024-05-01"));
//        paymentRepository.save(PaymentBuilder.buildPayment(account, category, paymentFrequency, "2024-05-05"));
//        Payment p3 = paymentRepository.save(PaymentBuilder.buildPayment(account, category, paymentFrequency, "2024-06-10"));
//        paymentRepository.save(PaymentBuilder.buildPayment(account, category, paymentFrequency, "2024-06-15"));
//        paymentRepository.save(PaymentBuilder.buildPayment(account, category, paymentFrequency, "2024-07-20"));
//
//        mockMvc.perform(delete(String.format("/v1/payments/%s", p3.getId()))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(new PaymentDeleteDTO(PaymentSelectionOption.ALL_PAYMENTS))))
//                .andExpect(status().isNoContent());
//
//        assertEquals(0, paymentRepository.count());
//        assertEquals(0, paymentFrequencyRepository.count());
//    }
//
//    @Test
//    @DisplayName("Deve retornar 400 ao tentar excluir um pagamento com tipo inexistente")
//    public void t14() throws Exception {
//        Account account = accountRepository.save(PaymentBuilder.buildPayment().getAccount());
//        PaymentCategory category = paymentCategoryRepository.save(PaymentBuilder.buildPayment().getPaymentCategory());
//        PaymentFrequency paymentFrequency = paymentFrequencyRepository.save(PaymentFrequencyBuilder.buildPaymentFrequency(4));
//
//        Payment payment = paymentRepository.save(PaymentBuilder.buildPayment(account, category, paymentFrequency, "2024-05-01"));
//
//        mockMvc.perform(delete(String.format("/v1/payments/%s", payment.getId()))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{\"type\": \"LOREM_IPSUM\"}"))
//                .andExpect(status().isBadRequest());
//
//        assertEquals(1, paymentRepository.count());
//        assertEquals(1, paymentFrequencyRepository.count());
//    }
//
//    @Test
//    @DisplayName("Deve retornar 404 ao tentar editar pagamento inexistente")
//    public void t15() throws Exception {
//        mockMvc.perform(put(String.format("/v1/payments/%s", 1))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(new PaymentEditionRequestDTO())))
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.message", is("Payment not found")));
//    }
//
//    @Test
//    @DisplayName("Deve retornar 404 ao tentar editar pagamento com uma categoria inexistente")
//    public void t16() throws Exception {
//        Account account = accountRepository.save(PaymentBuilder.buildPayment().getAccount());
//        PaymentCategory category = paymentCategoryRepository.save(PaymentBuilder.buildPayment().getPaymentCategory());
//        PaymentFrequency paymentFrequency = paymentFrequencyRepository.save(PaymentFrequencyBuilder.buildPaymentFrequency(4));
//        Payment payment = paymentRepository.save(PaymentBuilder.buildPayment(account, category, paymentFrequency, "2024-05-01"));
//
//        PaymentEditionRequestDTO request = PaymentEditionRequestDTO.builder()
//                .paymentCategoryId(1L)
//                .build();
//
//        mockMvc.perform(put(String.format("/v1/payments/%s", payment.getId()))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.message", is("Payment Category not found")));
//    }
//
//    @Test
//    @DisplayName("Deve retornar 400 ao tentar editar pagamento com valor inválido")
//    public void t17() throws Exception {
//        Account account = accountRepository.save(PaymentBuilder.buildPayment().getAccount());
//        PaymentCategory category = paymentCategoryRepository.save(PaymentBuilder.buildPayment().getPaymentCategory());
//        PaymentFrequency paymentFrequency = paymentFrequencyRepository.save(PaymentFrequencyBuilder.buildPaymentFrequency(4));
//        Payment payment = paymentRepository.save(PaymentBuilder.buildPayment(account, category, paymentFrequency, "2024-05-01"));
//
//        PaymentEditionRequestDTO request = PaymentEditionRequestDTO.builder()
//                .paymentCategoryId(category.getId())
//                .amount(-10)
//                .build();
//
//        mockMvc.perform(put(String.format("/v1/payments/%s", payment.getId()))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.message", is("Amount should be greater or equals 0.")));
//    }
//
//    @Test
//    @DisplayName("Deve retornar 204 ao editar pagamento com parâmetros válidos")
//    public void t18() throws Exception {
//        Account account = accountRepository.save(PaymentBuilder.buildPayment().getAccount());
//        PaymentCategory category = paymentCategoryRepository.save(PaymentBuilder.buildPayment().getPaymentCategory());
//        PaymentFrequency paymentFrequency = paymentFrequencyRepository.save(PaymentFrequencyBuilder.buildPaymentFrequency(4));
//        Payment payment = paymentRepository.save(PaymentBuilder.buildPayment(account, category, paymentFrequency, "2024-05-01"));
//
//        ArgumentCaptor<Payment> paymentArgumentCaptor = ArgumentCaptor.forClass(Payment.class);
//        ArgumentCaptor<PaymentSelectionOption> selectionTypeCaptor = ArgumentCaptor.forClass(PaymentSelectionOption.class);
//        doNothing().when(paymentEditionQueueProducer).processPaymentUpdate(paymentArgumentCaptor.capture(), selectionTypeCaptor.capture());
//
//        PaymentEditionRequestDTO request = PaymentEditionRequestDTO.builder()
//                .paymentCategoryId(category.getId())
//                .amount(10)
//                .type(PaymentSelectionOption.THIS_PAYMENT)
//                .deadlineAt(LocalDate.parse("2024-05-30"))
//                .isExpense(false)
//                .payed(false)
//                .description("Lorem Ipsum")
//                .build();
//
//        mockMvc.perform(put(String.format("/v1/payments/%s", payment.getId()))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isNoContent());
//
//        verify(paymentEditionQueueProducer, times(1)).processPaymentUpdate(any(Payment.class), eq(PaymentSelectionOption.THIS_PAYMENT));
//        assertEquals(account.getId(), paymentArgumentCaptor.getValue().getAccount().getId());
//        assertEquals(category.getId(), paymentArgumentCaptor.getValue().getPaymentCategory().getId());
//        assertEquals(LocalDate.parse("2024-05-30"), paymentArgumentCaptor.getValue().getDeadlineAt());
//        assertEquals(false, paymentArgumentCaptor.getValue().getIsExpense());
//        assertEquals(false, paymentArgumentCaptor.getValue().getPayed());
//        assertEquals("Lorem Ipsum", paymentArgumentCaptor.getValue().getDescription());
//        assertEquals(selectionTypeCaptor.getValue(), PaymentSelectionOption.THIS_PAYMENT);
//    }
}