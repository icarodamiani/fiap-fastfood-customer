package io.fiap.fastfood.driven.core.domain.customer.mapper;

import io.fiap.fastfood.driven.core.domain.model.Customer;
import io.fiap.fastfood.driven.core.entity.CustomerEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    CustomerEntity entityFromDomain(Customer customer);

    Customer domainFromEntity(CustomerEntity customerEntity);
}
