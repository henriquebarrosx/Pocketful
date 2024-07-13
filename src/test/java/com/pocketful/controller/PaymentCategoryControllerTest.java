package com.pocketful.controller;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
class PaymentCategoryControllerTest {
//    @Autowired
//    private PaymentCategoryRepository repository;
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    private ObjectMapper objectMapper;
//
//    @BeforeEach
//    public void setup() {
//        objectMapper = JsonMapper.builder()
//                .addModule(new JavaTimeModule())
//                .build();
//
//        repository.deleteAll();
//    }
//
//    @Test
//    @DisplayName("deve retornar 409 ao cadastrar categoria já existente")
//    public void t1() throws Exception {
//        NewPaymentCategoryDTO request = new NewPaymentCategoryDTO("Saúde");
//        repository.save(PaymentCategoryBuilder.buildPaymentCategory());
//
//        mockMvc.perform(post("/v1/payments/categories")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isConflict())
//                .andExpect(jsonPath("$.message", is("Payment category already exists.")));
//
//        assertEquals(1, repository.count());
//    }
//
//    @Test
//    @DisplayName("deve retornar 201 ao cadastrar categoria não já existente")
//    public void t2() throws Exception {
//        NewPaymentCategoryDTO request = new NewPaymentCategoryDTO("Saúde");
//
//        mockMvc.perform(post("/v1/payments/categories")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.id").exists());
//
//        assertEquals(1, repository.count());
//    }
//
//    @Test
//    @DisplayName("deve retornar 404 ao editar categoria inexistente")
//    public void t3() throws Exception {
//        NewPaymentCategoryDTO request = new NewPaymentCategoryDTO("Saúde");
//
//        mockMvc.perform(put("/v1/payments/categories/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.message", is("Payment Category not found")));
//
//        assertEquals(0, repository.count());
//    }
//
//    @Test
//    @DisplayName("deve retornar 409 ao editar uma categoria com nome repetido")
//    public void t4() throws Exception {
//        NewPaymentCategoryDTO request = new NewPaymentCategoryDTO("Saúde");
//        PaymentCategory paymentCategory = repository.save(PaymentCategoryBuilder.buildPaymentCategory());
//
//        mockMvc.perform(put("/v1/payments/categories/" + paymentCategory.getId().intValue())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isConflict())
//                .andExpect(jsonPath("$.message", is("Payment category already exists.")));
//
//        assertEquals(1, repository.count());
//    }
//
//    @Test
//    @DisplayName("deve retornar 200 ao editar uma categoria existente com nome não repetido")
//    public void t5() throws Exception {
//        NewPaymentCategoryDTO request = new NewPaymentCategoryDTO("Cartão de Crédito");
//        PaymentCategory paymentCategory = repository.save(PaymentCategoryBuilder.buildPaymentCategory());
//
//        mockMvc.perform(put("/v1/payments/categories/" + paymentCategory.getId().intValue())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id", is(paymentCategory.getId().intValue())))
//                .andExpect(jsonPath("$.name", is("Cartão de Crédito")));
//
//        assertEquals(1, repository.count());
//    }
//
//    @Test
//    @DisplayName("deve retornar 404 ao buscar por categoria inexistente")
//    public void t6() throws Exception {
//        mockMvc.perform(get("/v1/payments/categories/1")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.message", is("Payment Category not found")));
//    }
//
//    @Test
//    @DisplayName("deve retornar 200 ao buscar por categoria existente")
//    public void t7() throws Exception {
//        PaymentCategory paymentCategory = repository.save(PaymentCategoryBuilder.buildPaymentCategory());
//
//        mockMvc.perform(get("/v1/payments/categories/" + paymentCategory.getId().intValue())
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id", is(paymentCategory.getId().intValue())))
//                .andExpect(jsonPath("$.name", is("Saúde")));
//    }
//
//    @Test
//    @DisplayName("deve retornar 200 ao buscar por todas categorias existente")
//    public void t8() throws Exception {
//        PaymentCategory paymentCategory = repository.save(PaymentCategoryBuilder.buildPaymentCategory());
//
//        mockMvc.perform(get("/v1/payments/categories")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(1)))
//                .andExpect(jsonPath("$[0].id", is(paymentCategory.getId().intValue())))
//                .andExpect(jsonPath("$[0].name", is("Saúde")));
//    }
//
//    @Test
//    @DisplayName("deve retornar 404 ao deletar categoria inexistente")
//    public void t9() throws Exception {
//        mockMvc.perform(delete("/v1/payments/categories/1")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.message", is("Payment Category not found")));
//
//        assertEquals(0, repository.count());
//    }
//
//    @Test
//    @DisplayName("deve retornar 204 ao deletar categoria existente")
//    public void t10() throws Exception {
//        PaymentCategory paymentCategory = repository.save(PaymentCategoryBuilder.buildPaymentCategory());
//
//        mockMvc.perform(delete("/v1/payments/categories/" + paymentCategory.getId().intValue())
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNoContent());
//
//        assertEquals(0, repository.count());
//    }
}