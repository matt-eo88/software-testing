package com.amigoscode.testing.customer;

import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerRegistrationService {

    private final CustomerRepository customerRepository;

    public CustomerRegistrationService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public void registerNewCustomer(CustomerRegistrationRequest request) {
        Customer toRegister = request.getCustomer();
        Optional<Customer> maybeCustomer = customerRepository.
                selectCustomerByPhoneNumber(toRegister.getPhoneNumber());
        if (maybeCustomer.isPresent()) {
            Customer c = maybeCustomer.get();
            if (c.getName().equals(toRegister.getName())) {
                return;
            }
            throw new IllegalStateException("The phone number is already taken");
        }

        if (request.getCustomer().getId() == null) {
            request.getCustomer().setId(UUID.randomUUID());
        }
        customerRepository.save(toRegister);
    }
}
