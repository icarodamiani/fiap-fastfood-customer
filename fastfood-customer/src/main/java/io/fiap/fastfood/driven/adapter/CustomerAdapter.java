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
import io.fiap.fastfood.driven.core.messaging.MessagingPort;
import io.fiap.fastfood.driven.repository.CustomerRepository;
import io.vavr.CheckedFunction1;
import io.vavr.CheckedFunction2;
import io.vavr.Function1;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.model.Message;

@Component
public class CustomerAdapter implements CustomerPort {
    private final String queue;
    private final CustomerRepository customerRepository;
    private final MessagingPort messagingPort;
    private final CustomerMapper mapper;
    private final ObjectMapper objectMapper;

    public CustomerAdapter(@Value("${aws.sqs.customer.queue}") String queue,
                           CustomerRepository customerRepository,
                           MessagingPort messagingPort,
                           CustomerMapper mapper,
                           ObjectMapper objectMapper) {
        this.queue = queue;
        this.customerRepository = customerRepository;
        this.messagingPort = messagingPort;
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
    public Mono<Void> delete(String vat) {
        return customerRepository.deleteById(vat);
    }

    @Override
    public Mono<Void> deleteByVat(String vat) {
        return customerRepository.deleteByVat(vat);
    }

    @Override
    public Flux<Message> readCustomer(Function1<Customer, Mono<Customer>> handle) {
        return messagingPort.read(queue, handle, readEvent());
    }

    private CheckedFunction1<Message, Customer> readEvent() {
        return message -> objectMapper.readValue(message.body(), Customer.class);
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
