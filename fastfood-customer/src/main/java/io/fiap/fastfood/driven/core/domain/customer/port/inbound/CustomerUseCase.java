package io.fiap.fastfood.driven.core.domain.customer.port.inbound;

import io.fiap.fastfood.driven.core.domain.model.Customer;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.model.Message;

public interface CustomerUseCase {
    Flux<Message> handleEvent();
    Mono<Customer> save(Customer value);
    Mono<Void> delete(String id);
    Mono<Customer> update(String id, String operations);
    Flux<Customer> find(Pageable pageable);
    Mono<Customer> findById(String id);
    Mono<Customer> findByEmail(String email);
    Mono<Customer> findByVat(String vat);
}
