package com.amigoscode.testing.customer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CustomerRepositoryTest {
    
    @Autowired
    private CustomerRepository underTest;

    @Test
    void itShouldSelectCustomerByPhoneNumber() {
        // Given
        // When
        // Then  
    }

    @Test
    void itShouldSaveCustomer() {
        // Given
        Customer customer = new Customer(UUID.randomUUID(), "Mad Max", "0000");
        // When
        underTest.save(customer);
        // Then
        Optional<Customer> optionalCustomer = underTest.findById(customer.getId());
        assertThat(optionalCustomer).isPresent()
                .hasValueSatisfying(c -> {
                    /*assertThat(c.getId()).isEqualTo(customer.getId());*/
                    /*assertThat(c.getName()).isEqualTo(customer.getName());*/
                    /*assertThat(c.getPhoneNumber()).isEqualTo(customer.getPhoneNumber());*/
                    assertThat(c).isEqualToComparingFieldByField(customer);
                });
    }
}