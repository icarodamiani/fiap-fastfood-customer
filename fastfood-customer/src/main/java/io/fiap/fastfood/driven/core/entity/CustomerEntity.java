package io.fiap.fastfood.driven.core.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("customer")
public record CustomerEntity(
    @Id
    Integer id,
    String name,
    String vat,
    String email,
    String phone) {

    public static final class CustomerEntityBuilder {
        private Integer id;
        private String name;
        private String vat;
        private String email;
        private String phone;

        private CustomerEntityBuilder() {
        }

        public static CustomerEntityBuilder builder() {
            return new CustomerEntityBuilder();
        }

        public CustomerEntityBuilder withId(Integer id) {
            this.id = id;
            return this;
        }

        public CustomerEntityBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public CustomerEntityBuilder withVat(String vat) {
            this.vat = vat;
            return this;
        }

        public CustomerEntityBuilder withEmail(String email) {
            this.email = email;
            return this;
        }

        public CustomerEntityBuilder withPhone(String phone) {
            this.phone = phone;
            return this;
        }

        public CustomerEntity build() {
            return new CustomerEntity(id, name, vat, email, phone);
        }
    }
}

