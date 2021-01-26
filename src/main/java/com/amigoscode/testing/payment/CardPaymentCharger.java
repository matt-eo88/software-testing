package com.amigoscode.testing.payment;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
public interface CardPaymentCharger {

    CardPaymentCharge chargeCard(String cardSource,
                                 BigDecimal amount,
                                 Currency currency,
                                 String description);
}
