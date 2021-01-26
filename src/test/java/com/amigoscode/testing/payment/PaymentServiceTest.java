package com.amigoscode.testing.payment;

import com.amigoscode.testing.customer.Customer;
import com.amigoscode.testing.customer.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

class PaymentServiceTest {

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private CardPaymentCharger cardPaymentCharger;

    private PaymentService underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        underTest = new PaymentService(customerRepository, paymentRepository,
                cardPaymentCharger);
    }

    @Test
    void itShould_Charge_Card_Successfully() {
        // Given
        UUID customerId = UUID.randomUUID();
        given(customerRepository.findById(customerId))
                .willReturn(Optional.of(mock(Customer.class)));
        Currency currency = Currency.USD;
        PaymentRequest request = new PaymentRequest(new Payment(
                null, customerId, new BigDecimal("100.00"),
                currency, "card123xx", "Donation"
        ));

        given(cardPaymentCharger.chargeCard(request.getPayment().getSource(),
                request.getPayment().getAmount(),
                request.getPayment().getCurrency(),
                request.getPayment().getDescription())).willReturn(new CardPaymentCharge(true));
        // When
        underTest.chargeCard(customerId, request);
        // Then
        ArgumentCaptor<Payment> paymentRequestArgumentCaptor =
                ArgumentCaptor.forClass(Payment.class);

        then(paymentRepository).should().save(paymentRequestArgumentCaptor.capture());

        Payment payment = paymentRequestArgumentCaptor.getValue();
        assertThat(payment).isEqualToIgnoringGivenFields(request.getPayment(), "customerId");
        assertThat(payment.getCustomerId()).isEqualTo(customerId);
    }

    @Test
    void itShould_Throw_WhenCard_Not_Charged() {
        // Given
        UUID customerId = UUID.randomUUID();
        given(customerRepository.findById(customerId))
                .willReturn(Optional.of(mock(Customer.class)));
        Currency currency = Currency.USD;
        PaymentRequest request = new PaymentRequest(new Payment(
                null, customerId, new BigDecimal("100.00"),
                currency, "card123xx", "Donation"
        ));
        given(cardPaymentCharger.chargeCard(request.getPayment().getSource(),
                request.getPayment().getAmount(),
                request.getPayment().getCurrency(),
                request.getPayment().getDescription())).willReturn(new CardPaymentCharge(false));
        // When
        assertThatThrownBy(() -> underTest.chargeCard(customerId, request))
            .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Card not debited");
        // Then
        then(paymentRepository).should(never()).save(any(Payment.class));

    }

    @Test
    void itShould_Not_ChargeCard_AndThrow_When_CurrencyNotSupported() {
        UUID customerId = UUID.randomUUID();
        given(customerRepository.findById(customerId))
                .willReturn(Optional.of(mock(Customer.class)));
        Currency currency = Currency.EUR;
        PaymentRequest request = new PaymentRequest(new Payment(
                null, customerId, new BigDecimal("100.00"),
                currency, "card123xx", "Donation"
        ));

        assertThatThrownBy(() -> underTest.chargeCard(customerId, request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Currency not supported");
        // Then

        then(cardPaymentCharger).shouldHaveNoInteractions();

        // When
        underTest.chargeCard(customerId, request);
        // Then
        then(paymentRepository).shouldHaveNoInteractions();
    }

    @Test
    void itShould_NotCharge_WhenCustomer_NotFound() {
        // Given
        UUID customerId = UUID.randomUUID();
        given(customerRepository.findById(customerId)).willReturn(Optional.empty());
        // When
        assertThatThrownBy(() -> underTest.chargeCard(customerId, new PaymentRequest(new Payment())))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Customer not found");
        // Then
        then(cardPaymentCharger).shouldHaveNoInteractions();
        then(paymentRepository).shouldHaveNoInteractions();

    }
}