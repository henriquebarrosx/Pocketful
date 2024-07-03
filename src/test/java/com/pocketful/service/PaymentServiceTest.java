package com.pocketful.service;

import com.pocketful.entity.Account;
import com.pocketful.entity.Payment;
import com.pocketful.entity.PaymentCategory;
import com.pocketful.entity.PaymentFrequency;
import com.pocketful.enums.PaymentSelectionOption;
import com.pocketful.repository.AccountRepository;
import com.pocketful.repository.PaymentCategoryRepository;
import com.pocketful.repository.PaymentFrequencyRepository;
import com.pocketful.repository.PaymentRepository;
import com.pocketful.utils.AccountBuilder;
import com.pocketful.utils.PaymentBuilder;
import com.pocketful.utils.PaymentFrequencyBuilder;
import freemarker.template.Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
class PaymentServiceTest {
    @Autowired
    private PaymentRepository paymentRepository;

    @MockBean
    private EmailService emailService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentCategoryRepository paymentCategoryRepository;

    @Autowired
    private PaymentFrequencyRepository paymentFrequencyRepository;

    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    public void setup() {
        paymentRepository.deleteAll();
        paymentFrequencyRepository.deleteAll();
        paymentCategoryRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    @DisplayName("deve repassar para o service de email apenas os pagamentos pendentes, não pagos, vencidos ou à vencer")
    void shouldReturnOnlyPendingPayments() {
        LocalDate date = LocalDate.parse("2024-10-12");

        Account account1 = accountRepository.save(AccountBuilder.buildAccount("John Doe", "john.doe@mail.com", "5582988776655"));
        Account account2 = accountRepository.save(AccountBuilder.buildAccount("Anne Wilkes", "anne.wilkes@mail.com", "5582988776654"));
        PaymentCategory category = paymentCategoryRepository.save(PaymentBuilder.buildPayment().getPaymentCategory());
        PaymentFrequency paymentFrequency = paymentFrequencyRepository.save(PaymentFrequencyBuilder.buildPaymentFrequency(4));

        paymentRepository.saveAll(
                List.of(
                        paymentRepository.save(PaymentBuilder.buildPayment(account1, category, paymentFrequency, "2024-10-12", 10)),
                        paymentRepository.save(PaymentBuilder.buildPayment(account2, category, paymentFrequency, "2024-10-10", 10)),
                        paymentRepository.save(PaymentBuilder.buildPayment(account1, category, paymentFrequency, "2024-10-08", 10))
                )
        );

        when(emailService.getTemplate("lembrete-vencimento-html.ftl")).thenReturn(mock(Template.class));
        doNothing().when(emailService).send(anyString(), anyString(), any(), any(), anyMap());

        ArgumentCaptor<String> receiverCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Template> templateCaptor = ArgumentCaptor.forClass(Template.class);
        ArgumentCaptor<Map<Account, Payment>> modelCaptor = ArgumentCaptor.forClass(Map.class);

        paymentService.notifyPendingPaymentsByDate(date);

        verify(emailService, times(2))
                .send(receiverCaptor.capture(), subjectCaptor.capture(), templateCaptor.capture(), templateCaptor.capture(), modelCaptor.capture());

        List<String> receivers = receiverCaptor.getAllValues();
        List<String> subjects = subjectCaptor.getAllValues();
        List<Map<Account, Payment>> models = modelCaptor.getAllValues();

        assertEquals(2, receivers.size());
        assertTrue(receivers.contains(account1.getEmail()));
        assertTrue(receivers.contains(account2.getEmail()));

        assertEquals(2, subjects.size());
        assertEquals("Lembrete de Vencimento", subjects.get(0));
        assertEquals("Lembrete de Vencimento", subjects.get(1));

        assertEquals(2, models.size());

        assertThat(models).anySatisfy(model -> {
            Object accountObject = model.get("account");
            assertThat(accountObject).isInstanceOf(Account.class);

            Account account = (Account) accountObject;
            assertThat(account1.getId()).isEqualTo(account.getId());
        });

        assertThat(models).anySatisfy(model -> {
            Object accountObject = model.get("account");
            assertThat(accountObject).isInstanceOf(Account.class);

            Account account = (Account) accountObject;
            assertThat(account2.getId()).isEqualTo(account.getId());
        });
    }

    @Test
    @DisplayName("deve cadastrar N - 1 pagamentos")
    void shouldGeneratePayments() {
        int times = 10;

        Account account = accountRepository.save(PaymentBuilder.buildPayment().getAccount());
        PaymentCategory category = paymentCategoryRepository.save(PaymentBuilder.buildPayment().getPaymentCategory());
        PaymentFrequency paymentFrequency = paymentFrequencyRepository.save(PaymentFrequencyBuilder.buildPaymentFrequency(times));
        Payment payment = PaymentBuilder.buildPayment(account, category, paymentFrequency);

        paymentService.processPaymentGeneration(payment);

        assertEquals(times - 1, paymentRepository.count());
    }

    @Test
    @DisplayName("deve atualizar somente o pagamento passado via parâmetro quando o tipo de seleção for PaymentSelectionOption.THIS_PAYMENT")
    void shouldUpdatePayments() {
        Account account = accountRepository.save(PaymentBuilder.buildPayment().getAccount());
        PaymentCategory category = paymentCategoryRepository.save(PaymentBuilder.buildPayment().getPaymentCategory());
        PaymentFrequency paymentFrequency = paymentFrequencyRepository.save(PaymentFrequencyBuilder.buildPaymentFrequency(4));

        Payment p1 = paymentRepository.save(PaymentBuilder.buildPayment(account, category, paymentFrequency, "2024-06-10", 10));
        Payment p2 = paymentRepository.save(PaymentBuilder.buildPayment(account, category, paymentFrequency, "2024-05-01", 10));
        Payment p3 = paymentRepository.save(PaymentBuilder.buildPayment(account, category, paymentFrequency, "2024-05-05", 10));

        p1.setAmount(100);
        paymentService.processPaymentEdition(p1, PaymentSelectionOption.THIS_PAYMENT);

        assertEquals(100.0, paymentService.findById(p1.getId()).getAmount());
        assertEquals(10.0, paymentService.findById(p2.getId()).getAmount());
        assertEquals(10.0, paymentService.findById(p3.getId()).getAmount());
    }

    @Test
    @DisplayName("deve atualizar todos os pagamentos com data de vencimento igual o superior a do pagamento fornecido quando o tipo de seleção for PaymentSelectionOption.THIS_AND_FUTURE_PAYMENTS")
    void shouldUpdateCurrentAndFuturePayments() {
        Account account = accountRepository.save(PaymentBuilder.buildPayment().getAccount());
        PaymentCategory category = paymentCategoryRepository.save(PaymentBuilder.buildPayment().getPaymentCategory());
        PaymentFrequency paymentFrequency = paymentFrequencyRepository.save(PaymentFrequencyBuilder.buildPaymentFrequency(4));

        Payment p1 = paymentRepository.save(PaymentBuilder.buildPayment(account, category, paymentFrequency, "2024-05-10", 10));
        Payment p2 = paymentRepository.save(PaymentBuilder.buildPayment(account, category, paymentFrequency, "2024-06-15", 10));
        Payment p3 = paymentRepository.save(PaymentBuilder.buildPayment(account, category, paymentFrequency, "2024-07-20", 10));

        p2.setAmount(100);
        paymentService.processPaymentEdition(p2, PaymentSelectionOption.THIS_AND_FUTURE_PAYMENTS);

        assertEquals(10.0, paymentService.findById(p1.getId()).getAmount());
        assertEquals(100.0, paymentService.findById(p2.getId()).getAmount());
        assertEquals(100.0, paymentService.findById(p3.getId()).getAmount());

        assertEquals(paymentFrequency.getId(), paymentService.findById(p1.getId()).getPaymentFrequency().getId());
        assertEquals(paymentFrequency.getId(), paymentService.findById(p2.getId()).getPaymentFrequency().getId());
        assertEquals(paymentFrequency.getId(), paymentService.findById(p3.getId()).getPaymentFrequency().getId());

        assertEquals(LocalDate.parse("2024-05-10"), paymentService.findById(p1.getId()).getDeadlineAt());
        assertEquals(LocalDate.parse("2024-06-15"), paymentService.findById(p2.getId()).getDeadlineAt());
        assertEquals(LocalDate.parse("2024-07-15"), paymentService.findById(p3.getId()).getDeadlineAt());
    }

    @Test
    @DisplayName("deve atualizar todos os pagamentos quando o tipo de seleção for PaymentSelectionOption.ALL_PAYMENTS")
    void shouldUpdateAllPayments() {
        Account account = accountRepository.save(PaymentBuilder.buildPayment().getAccount());
        PaymentCategory category = paymentCategoryRepository.save(PaymentBuilder.buildPayment().getPaymentCategory());
        PaymentFrequency paymentFrequency = paymentFrequencyRepository.save(PaymentFrequencyBuilder.buildPaymentFrequency(4));

        Payment p1 = paymentRepository.save(PaymentBuilder.buildPayment(account, category, paymentFrequency, "2024-05-10", 10));
        Payment p2 = paymentRepository.save(PaymentBuilder.buildPayment(account, category, paymentFrequency, "2024-06-15", 10));
        Payment p3 = paymentRepository.save(PaymentBuilder.buildPayment(account, category, paymentFrequency, "2024-07-20", 10));

        p2.setAmount(100);
        paymentService.processPaymentEdition(p2, PaymentSelectionOption.ALL_PAYMENTS);

        assertEquals(100.0, paymentService.findById(p1.getId()).getAmount());
        assertEquals(100.0, paymentService.findById(p2.getId()).getAmount());
        assertEquals(100.0, paymentService.findById(p3.getId()).getAmount());

        assertEquals(paymentFrequency.getId(), paymentService.findById(p1.getId()).getPaymentFrequency().getId());
        assertEquals(paymentFrequency.getId(), paymentService.findById(p2.getId()).getPaymentFrequency().getId());
        assertEquals(paymentFrequency.getId(), paymentService.findById(p3.getId()).getPaymentFrequency().getId());

        assertEquals(LocalDate.parse("2024-05-15"), paymentService.findById(p1.getId()).getDeadlineAt());
        assertEquals(LocalDate.parse("2024-06-15"), paymentService.findById(p2.getId()).getDeadlineAt());
        assertEquals(LocalDate.parse("2024-07-15"), paymentService.findById(p3.getId()).getDeadlineAt());
    }
}