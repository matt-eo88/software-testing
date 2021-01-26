package com.amigoscode.testing.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

class CustomerRegistrationServiceTest {

    @Captor
    private ArgumentCaptor<Customer> argumentCaptor;

    private CustomerRegistrationService underTest;
    @Mock
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        underTest = new CustomerRegistrationService(customerRepository);
    }

    @Test
    void itShouldSaveNewCustomer() {
        // Given
        String phoneNumber = "00099";
        Customer customer = new Customer(UUID.randomUUID(), "Maria", phoneNumber);

        CustomerRegistrationRequest customerRegistrationRequest =
                new CustomerRegistrationRequest(customer);

        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber))
                .willReturn(Optional.empty());
        // When
        underTest.registerNewCustomer(customerRegistrationRequest);

        // Then
        then(customerRepository).should().save(argumentCaptor.capture());
        Customer argCap = argumentCaptor.getValue();
        assertThat(argCap).isEqualTo(customer);
    }

    @Test
    void itShouldSaveNewCustomerWhenIdIsNull() {
        // Given
        String phoneNumber = "00099";
        Customer customer = new Customer(null, "Maria", phoneNumber);

        CustomerRegistrationRequest customerRegistrationRequest =
                new CustomerRegistrationRequest(customer);

        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber))
                .willReturn(Optional.empty());
        // When
        underTest.registerNewCustomer(customerRegistrationRequest);

        // Then
        then(customerRepository).should().save(argumentCaptor.capture());
        Customer argCap = argumentCaptor.getValue();
        assertThat(argCap).isEqualToIgnoringGivenFields(customer, "id");
        assertThat(argCap.getId()).isNotNull();

    }

    @Test
    void itShouldNotSaveCustomerWhenCustomerExists() {
        // Given
        String phoneNumber = "00099";
        Customer customer = new Customer(UUID.randomUUID(), "Maria", phoneNumber);

        CustomerRegistrationRequest customerRegistrationRequest =
                new CustomerRegistrationRequest(customer);

        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber))
                .willReturn(Optional.of(customer));
        // When
        underTest.registerNewCustomer(customerRegistrationRequest);
        // Then
        then(customerRepository).should(never()).save(any());
    }

    @Test
    void itShouldThrowWhenPhoneNumberTaken() {
        // Given
        // Given
        String phoneNumber = "00099";
        Customer customer = new Customer(UUID.randomUUID(), "Maria", phoneNumber);
        Customer customer2 = new Customer(UUID.randomUUID(), "John", phoneNumber);


        CustomerRegistrationRequest customerRegistrationRequest =
                new CustomerRegistrationRequest(customer);

        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber))
                .willReturn(Optional.of(customer2));

        // When
        assertThatThrownBy(() -> underTest.registerNewCustomer(customerRegistrationRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("The phone number is already taken");
        // Then
        then(customerRepository).should(never()).save(any(Customer.class));
    }
}