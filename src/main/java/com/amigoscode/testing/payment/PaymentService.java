package com.amigoscode.testing.payment;

import com.amigoscode.testing.customer.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {

    private static final List<Currency> ACCEPTED_CURRENCIES =
            List.of(Currency.GBP, Currency.USD);
    private final CustomerRepository customerRepository;
    private final PaymentRepository paymentRepository;
    private final CardPaymentCharger cardPaymentCharger;

    @Autowired
    public PaymentService(CustomerRepository customerRepository,
                          PaymentRepository paymentRepository,
                          CardPaymentCharger cardPaymentCharger) {
        this.customerRepository = customerRepository;
        this.paymentRepository = paymentRepository;
        this.cardPaymentCharger = cardPaymentCharger;
    }

    void chargeCard(UUID customerId, PaymentRequest request) {
        boolean isCustomerFound = customerRepository.findById(customerId)
                .isPresent();
        if (!isCustomerFound) {
            throw new IllegalStateException("Customer not found");
        }

        boolean isCurrencySupported = ACCEPTED_CURRENCIES.stream()
                .anyMatch(c -> c.equals(request.getPayment().getCurrency()));
        if (!isCurrencySupported) {
            String message = "Currency not supported";
            throw new IllegalStateException(message);
        }

        CardPaymentCharge cardPaymentCharge = cardPaymentCharger.chargeCard(
                request.getPayment().getSource(),
                request.getPayment().getAmount(),
                request.getPayment().getCurrency(),
                request.getPayment().getDescription()
        );

        if (!cardPaymentCharge.isCardDebited()) {
            throw new IllegalStateException("Card not debited");
        }

        request.getPayment().setCustomerId(customerId);

        paymentRepository.save(request.getPayment());
    }
}
