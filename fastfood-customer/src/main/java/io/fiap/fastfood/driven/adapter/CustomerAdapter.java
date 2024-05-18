package io.fiap.fastfood.driven.adapter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import io.fiap.fastfood.driven.core.domain.customer.mapper.CustomerMapper;
import io.fiap.fastfood.driven.core.domain.customer.port.outbound.CustomerPort;
import io.fiap.fastfood.driven.core.domain.model.Customer;
import io.fiap.fastfood.driven.core.entity.CustomerEntity;
import io.fiap.fastfood.driven.core.exception.BadRequestException;
import io.fiap.fastfood.driven.core.exception.DuplicatedKeyException;
import io.fiap.fastfood.driven.repository.CustomerRepository;
import io.vavr.CheckedFunction1;
import io.vavr.CheckedFunction2;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class CustomerAdapter implements CustomerPort {
    private final CustomerRepository customerRepository;
    private final CustomerMapper mapper;
    private final ObjectMapper objectMapper;

    public CustomerAdapter(CustomerRepository customerRepository,
                           CustomerMapper mapper,
                           ObjectMapper objectMapper) {
        this.customerRepository = customerRepository;
        this.mapper = mapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Customer> save(Customer customer) {
        return customerRepository.findCustomerByVat(customer.vat())
            .flatMap(c -> Mono.defer(() -> Mono.<CustomerEntity>error(DuplicatedKeyException::new)))
            .switchIfEmpty(Mono.defer(() -> customerRepository.save(mapper.entityFromDomain(customer))))
            .map(mapper::domainFromEntity);
    }

    @Override
    public Mono<Void> delete(String id) {
        return customerRepository.deleteById(id);
    }

    @Override
    public Mono<Customer> update(String id, String operations) {
        return customerRepository.findById(id)
            .map(customer -> applyPatch().unchecked().apply(customer, operations))
            .flatMap(customerRepository::save)
            .map(mapper::domainFromEntity)
            .onErrorMap(JsonPatchException.class::isInstance, BadRequestException::new);
    }

    private CheckedFunction2<CustomerEntity, String, CustomerEntity> applyPatch() {
        return (customer, operations) -> {
            var patch = readOperations()
                .unchecked()
                .apply(operations);

            var patched = patch.apply(objectMapper.convertValue(customer, JsonNode.class));

            return objectMapper.treeToValue(patched, CustomerEntity.class);
        };
    }

    private CheckedFunction1<String, JsonPatch> readOperations() {
        return operations -> {
            final InputStream in = new ByteArrayInputStream(operations.getBytes());
            return objectMapper.readValue(in, JsonPatch.class);
        };
    }

    @Override
    public Flux<Customer> find(Pageable pageable) {
        return customerRepository.findByIdNotNull(pageable)
            .map(mapper::domainFromEntity);
    }

    @Override
    public Mono<Customer> findById(String id) {
        return customerRepository.findById(id)
            .map(mapper::domainFromEntity);
    }

    @Override
    public Mono<Customer> findByEmail(String email) {
        return customerRepository.findByEmail(email)
            .map(mapper::domainFromEntity);
    }

    @Override
    public Mono<Customer> findByVat(String vat) {
        return customerRepository.findByVat(vat)
            .map(mapper::domainFromEntity);
    }
}
