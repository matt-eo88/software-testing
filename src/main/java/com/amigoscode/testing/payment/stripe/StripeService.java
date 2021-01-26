package com.amigoscode.testing.payment.stripe;

import com.amigoscode.testing.payment.CardPaymentCharge;
import com.amigoscode.testing.payment.CardPaymentCharger;
import com.amigoscode.testing.payment.Currency;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.net.RequestOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class StripeService implements CardPaymentCharger {

    private final static RequestOptions requestOptions = RequestOptions.builder()
            .setApiKey("sk_test_4eC39HqLyjWDarjtT1zdp7dc")
            .build();
    private final StripeApi stripeApi;

    @Autowired
    public StripeService(StripeApi stripeApi) {
        this.stripeApi = stripeApi;
    }

    @Override
    public CardPaymentCharge chargeCard(String cardSource, BigDecimal amount, Currency currency, String description) {
        Map<String, Object> params = new HashMap<>();
        params.put("amount", amount);
        params.put("currency", currency);
        params.put("source", cardSource);
        params.put(
                "description",
                description
        );

        try {
            Charge charge = stripeApi.create(params, requestOptions);
            Boolean chargePaid = charge.getPaid();
            return new CardPaymentCharge(chargePaid);
        } catch (StripeException e) {
            e.printStackTrace();
            throw new IllegalStateException("Cannot make stripe charge");
        }
    }
}
