package io.fiap.fastfood.driven.core.service;

import io.fiap.fastfood.driven.core.domain.customer.port.inbound.CustomerUseCase;
import io.fiap.fastfood.driven.core.domain.customer.port.outbound.CustomerPort;
import io.fiap.fastfood.driven.core.domain.model.Customer;
import io.fiap.fastfood.driven.core.exception.BadRequestException;
import io.vavr.Function1;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.model.Message;

@Service
public class CustomerService implements CustomerUseCase {

    private final CustomerPort customerPort;


    public CustomerService(CustomerPort customerPort) {
        this.customerPort = customerPort;
    }

    @Override
    public Flux<Message> handleEvent() {
        return customerPort.readCustomer(handle());
    }

    private Function1<Customer, Mono<Customer>> handle() {
        return customer -> Mono.just(customer)
            .flatMap(customerPort::save);
    }

    @Override
    public Mono<Customer> save(Customer customer) {
        return Mono.just(customer)
            .switchIfEmpty(Mono.defer(() -> Mono.error(new BadRequestException())))
            .flatMap(customerPort::save);
    }

    @Override
    public Mono<Customer> update(String id, String operations) {
        return customerPort.update(id, operations);
    }

    @Override
    public Mono<Void> delete(String vat) {
        return Mono.just(vat)
            .switchIfEmpty(Mono.defer(() -> Mono.error(new BadRequestException())))
            .flatMap(customerPort::deleteByVat);
    }

    @Override
    public Flux<Customer> find(Pageable pageable) {
        return customerPort.find(pageable);
    }

    @Override
    public Mono<Customer> findById(String id) {
        return customerPort.findById(id);
    }

    @Override
    public Mono<Customer> findByEmail(String email) {
        return customerPort.findByEmail(email);
    }

    @Override
    public Mono<Customer> findByVat(String vat) {
        return customerPort.findByEmail(vat);
    }
}
