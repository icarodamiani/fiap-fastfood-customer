package io.fiap.fastfood.driven.core.domain.customer.port.outbound;

import io.fiap.fastfood.driven.core.domain.model.Customer;
import io.vavr.Function1;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.model.Message;

public interface CustomerPort {
    Mono<Customer> save(Customer product);

    Mono<Customer> update(String id, String operations);

    Mono<Void> delete(String id);

    Mono<Void> deleteByVat(String vat);

    Flux<Customer> find(Pageable pageable);

    Mono<Customer> findById(String id);

    Mono<Customer> findByEmail(String email);

    Mono<Customer> findByVat(String vat);

    Flux<Message> readCustomer(Function1<Customer, Mono<Customer>> handle);
}
