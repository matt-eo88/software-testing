package com.amigoscode.testing.customer;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Entity
public class Customer {
    @Id
    private UUID id;
    @NotBlank
    private String name;
    @NotBlank
    private String number;

    public Customer() {}

    public Customer(UUID id, String name, String number) {
        this.id = id;
        this.name = name;
        this.number = number;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
