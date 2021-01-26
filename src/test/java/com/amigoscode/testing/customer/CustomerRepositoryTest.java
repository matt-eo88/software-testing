package com.amigoscode.testing.customer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(
        properties = {"spring.jpa.properties.javax.persistence.validation.mode=none"}
)
class CustomerRepositoryTest {
    
    @Autowired
    private CustomerRepository underTest;

    @Test
    void itShouldSelectCustomerByPhoneNumber() {
        // Given
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(id, "Mark", "0000");
        // When
        underTest.save(customer);
        // Then
        Optional<Customer> optionalCustomer = underTest
                .selectCustomerByPhoneNumber(customer.getPhoneNumber());
        assertThat(optionalCustomer).isPresent()
                .hasValueSatisfying(c -> assertThat(c).isEqualToComparingFieldByField(customer));
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
                    assertThat(c).isEqualToComparingFieldByField(customer);
                });
    }

    @Test
    void itShouldNotSelectCustomerByPhoneNumberWhenNumberDoesNotExist() {
        String number = "0000";

        Optional<Customer> optionalCustomer = underTest.selectCustomerByPhoneNumber(number);

        assertThat(optionalCustomer).isNotPresent();
    }

    @Test
    void itShouldNotSaveCustomerWhenNameIsNull() {
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(id, null, "0000");

        assertThatThrownBy(() -> underTest.save(customer))
                .hasMessageContaining("not-null property references a null value")
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void itShouldNotSaveCustomerWhenNumberIsNull() {
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(id, "Alex", null);

        assertThatThrownBy(() -> underTest.save(customer))
                .hasMessageContaining("not-null property references a null value")
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}