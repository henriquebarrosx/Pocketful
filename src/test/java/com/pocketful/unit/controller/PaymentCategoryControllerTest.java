package com.pocketful.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pocketful.config.SecurityConfig;
import com.pocketful.controller.PaymentCategoryController;
import com.pocketful.entity.Account;
import com.pocketful.entity.AccountRole;
import com.pocketful.entity.PaymentCategory;
import com.pocketful.service.AccountService;
import com.pocketful.service.PaymentCategoryService;
import com.pocketful.service.SessionManagerService;
import com.pocketful.utils.AccountBuilder;
import com.pocketful.utils.PaymentCategoryBuilder;
import com.pocketful.utils.SessionBuilder;
import com.pocketful.web.dto.payment_category.PaymentCategoryCreationRequestDTO;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;

import static com.pocketful.config.SecurityFilterConfig.AUTHORIZATION;

@Import(SecurityConfig.class)
@WebMvcTest(PaymentCategoryController.class)
public class PaymentCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PaymentCategoryService paymentCategoryService;

    @MockBean
    private AccountService accountService;

    @MockBean
    private SessionManagerService sessionManagerService;

    @Test
    void shouldThrowExceptionWhenGettingCategoriesAndHasDefaultRole() throws Exception {
        Account account = AccountBuilder.build(AccountRole.DEFAULT);
        String token = SessionBuilder.build(account);

        Mockito.when(accountService.findByEmail(account.getEmail()))
                .thenReturn(account);

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/payments/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, token))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void shouldReturnCategoriesWhenHasAdminRole() throws Exception {
        Account account = AccountBuilder.build(AccountRole.ADMIN);
        String token = SessionBuilder.build(account);

        Mockito.when(accountService.findByEmail(account.getEmail()))
                .thenReturn(account);

        Mockito.when(paymentCategoryService.findAll())
                .thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/payments/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(0)));
    }

    @Test
    void shouldThrowExceptionWhenGettingByIdHasDefaultRole() throws Exception {
        Account account = AccountBuilder.build(AccountRole.DEFAULT);
        String token = SessionBuilder.build(account);

        Mockito.when(accountService.findByEmail(account.getEmail()))
                .thenReturn(account);

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/payments/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, token))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void shouldReturnCategoriesWhenGettingByIdAndHasAdminRole() throws Exception {
        PaymentCategory category = PaymentCategoryBuilder.build();
        Account account = AccountBuilder.build(AccountRole.ADMIN);
        String token = SessionBuilder.build(account);

        Mockito.when(accountService.findByEmail(account.getEmail()))
                .thenReturn(account);

        Mockito.when(paymentCategoryService.findById(category.getId()))
                .thenReturn(category);

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/payments/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(category.getId().intValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(category.getName())));
    }

    @Test
    void shouldThrowExceptionWhenCreatingAndHasDefaultRole() throws Exception {
        Account account = AccountBuilder.build(AccountRole.DEFAULT);
        String token = SessionBuilder.build(account);

        Mockito.when(accountService.findByEmail(account.getEmail()))
                .thenReturn(account);

        mockMvc.perform(MockMvcRequestBuilders.post("/v1/payments/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, token))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void shouldReturnCategoriesWhenCreatingAndHasAdminRole() throws Exception {
        PaymentCategory category = PaymentCategoryBuilder.build();
        PaymentCategoryCreationRequestDTO request = new PaymentCategoryCreationRequestDTO(category.getName());
        Account account = AccountBuilder.build(AccountRole.ADMIN);
        String token = SessionBuilder.build(account);

        Mockito.when(accountService.findByEmail(account.getEmail()))
                .thenReturn(account);

        Mockito.when(paymentCategoryService.create(category.getName()))
                .thenReturn(category);

        mockMvc.perform(MockMvcRequestBuilders.post("/v1/payments/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, token)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(category.getId().intValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(category.getName())));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingAndHasDefaultRole() throws Exception {
        Account account = AccountBuilder.build(AccountRole.DEFAULT);
        String token = SessionBuilder.build(account);

        Mockito.when(accountService.findByEmail(account.getEmail()))
                .thenReturn(account);

        mockMvc.perform(MockMvcRequestBuilders.put("/v1/payments/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, token))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void shouldReturnCategoriesWhenUpdatingAndHasAdminRole() throws Exception {
        PaymentCategory category = PaymentCategoryBuilder.build();
        PaymentCategoryCreationRequestDTO request = new PaymentCategoryCreationRequestDTO(category.getName());
        Account account = AccountBuilder.build(AccountRole.ADMIN);
        String token = SessionBuilder.build(account);

        Mockito.when(accountService.findByEmail(account.getEmail()))
                .thenReturn(account);

        Mockito.when(paymentCategoryService.update(category.getId(), category.getName()))
                .thenReturn(category);

        mockMvc.perform(MockMvcRequestBuilders.put("/v1/payments/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, token)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(category.getId().intValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(category.getName())));
    }

    @Test
    void shouldThrowExceptionWhenDeletingAndHasDefaultRole() throws Exception {
        Account account = AccountBuilder.build(AccountRole.DEFAULT);
        String token = SessionBuilder.build(account);

        Mockito.when(accountService.findByEmail(account.getEmail()))
                .thenReturn(account);

        mockMvc.perform(MockMvcRequestBuilders.delete("/v1/payments/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, token))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void shouldReturnCategoriesWhenDeletingAndHasAdminRole() throws Exception {
        PaymentCategory category = PaymentCategoryBuilder.build();
        PaymentCategoryCreationRequestDTO request = new PaymentCategoryCreationRequestDTO(category.getName());
        Account account = AccountBuilder.build(AccountRole.ADMIN);
        String token = SessionBuilder.build(account);

        Mockito.when(accountService.findByEmail(account.getEmail())).thenReturn(account);
        Mockito.doNothing().when(paymentCategoryService).delete(category.getId());

        mockMvc.perform(MockMvcRequestBuilders.delete("/v1/payments/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, token)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}
