package io.fiap.fastfood.driven.repository;

import io.fiap.fastfood.driven.core.entity.CustomerEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomerRepository extends ReactiveCrudRepository<CustomerEntity, String> {

    Flux<CustomerEntity> findByIdNotNull(Pageable pageable);

    Mono<CustomerEntity> findCustomerByVat(String vat);

    Mono<CustomerEntity> findByEmail(String email);

    Mono<CustomerEntity> findByVat(String vat);
}
