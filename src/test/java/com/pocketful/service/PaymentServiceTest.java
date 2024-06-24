package com.pocketful.service;

import com.pocketful.entity.Account;
import com.pocketful.entity.Payment;
import com.pocketful.repository.PaymentRepository;
import com.pocketful.utils.PaymentBuilder;
import freemarker.template.Template;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private EmailService emailService;

    @Test
    @DisplayName("deve repassar para o service de email apenas os pagamentos pendentes, não pagos, vencidos ou à vencer")
    void shouldReturnOnlyPendingPayments() {
        LocalDate date = LocalDate.parse("2024-10-12");

        Account account1 = Account.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@mail.com")
                .phoneNumber("5582988776655")
                .build();

        Account account2 = Account.builder()
                .id(2L)
                .name("Anne Wilkes")
                .email("anne.wilkes@mail.com")
                .phoneNumber("5582988776654")
                .build();

        List<Payment> payments = List.of(
                PaymentBuilder.buildPayment(1L, LocalDate.parse("2024-10-12"), account1),
                PaymentBuilder.buildPayment(2L, LocalDate.parse("2024-10-10"), account2),
                PaymentBuilder.buildPayment(3L, LocalDate.parse("2024-10-08"), account1)
        );

        when(paymentRepository.findAllByDeadlineAtLessThanEqualAndPayedIsFalseAndIsExpenseIsTrue(date))
                .thenReturn(payments);

        when(emailService.getTemplate("email.ftl"))
                .thenReturn(mock(Template.class));

        doNothing()
                .when(emailService)
                .send(anyString(), anyString(), any(), anyMap());

        paymentService.notifyPendingPaymentsByDate(date);

        ArgumentCaptor<String> receiverCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Template> templateCaptor = ArgumentCaptor.forClass(Template.class);
        ArgumentCaptor<Map<String, Object>> modelCaptor = ArgumentCaptor.forClass(Map.class);

        verify(emailService, times(2))
                .send(receiverCaptor.capture(), subjectCaptor.capture(), templateCaptor.capture(), modelCaptor.capture());

        List<String> receivers = receiverCaptor.getAllValues();
        List<String> subjects = subjectCaptor.getAllValues();
        List<Map<String, Object>> models = modelCaptor.getAllValues();

        assertEquals(2, receivers.size());
        assertTrue(receivers.contains(account1.getEmail()));
        assertTrue(receivers.contains(account2.getEmail()));

        assertEquals(2, subjects.size());
        assertEquals("Lembrete de Vencimento", subjects.get(0));
        assertEquals("Lembrete de Vencimento", subjects.get(1));

        assertEquals(2, models.size());
        assertThat(models).anySatisfy(model -> assertThat(account1).isEqualTo(model.get("account")));
        assertThat(models).anySatisfy(model -> assertThat(account2).isEqualTo(model.get("account")));
    }
}