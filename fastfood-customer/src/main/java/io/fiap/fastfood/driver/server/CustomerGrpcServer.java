package io.fiap.fastfood.driver.server;


import com.google.protobuf.Empty;
import io.fiap.fastfood.CustomerResponse;
import io.fiap.fastfood.CustomerServiceGrpc;
import io.fiap.fastfood.DeleteCustomerRequest;
import io.fiap.fastfood.FindAllCustomerRequest;
import io.fiap.fastfood.FindCustomerByEmailRequest;
import io.fiap.fastfood.FindCustomerByIdRequest;
import io.fiap.fastfood.FindCustomerByVatRequest;
import io.fiap.fastfood.SaveCustomerRequest;
import io.fiap.fastfood.UpdateCustomerRequest;
import io.fiap.fastfood.driven.core.domain.model.Customer;
import io.fiap.fastfood.driven.core.service.CustomerService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

@GrpcService
public class CustomerGrpcServer extends CustomerServiceGrpc.CustomerServiceImplBase {

    private final CustomerService service;

    private final GrpcStatusConverter statusConverter;

    @Autowired
    public CustomerGrpcServer(CustomerService service, GrpcStatusConverter statusConverter) {
        this.service = service;
        this.statusConverter = statusConverter;
    }

    @Override
    public void saveCustomer(SaveCustomerRequest request, StreamObserver<CustomerResponse> responseObserver) {
        service.save(Customer.CustomerBuilder.builder()
                .withEmail(request.getEmail())
                .withName(request.getName())
                .withPhone(request.getPhone())
                .withVat(request.getVat())
                .build())
            .doOnError(throwable -> responseObserver.onError(statusConverter.toGrpcException(throwable)))
            .map(customer ->
                CustomerResponse.newBuilder()
                    .setId(customer.id().toString())
                    .build()
            )
            .map(response -> {
                responseObserver.onNext(response);
                responseObserver.onCompleted();
                return response;
            })
            .subscribe();
    }

    @Override
    public void updateCustomer(UpdateCustomerRequest request, StreamObserver<CustomerResponse> responseObserver) {
        service.update(request.getId(), request.getOperations())
            .doOnError(throwable -> responseObserver.onError(statusConverter.toGrpcException(throwable)))
            .map(customer ->
                CustomerResponse.newBuilder()
                    .setId(customer.id().toString())
                    .build()
            )
            .map(response -> {
                responseObserver.onNext(response);
                responseObserver.onCompleted();
                return response;
            })
            .subscribe();
    }

    @Override
    public void deleteCustomer(DeleteCustomerRequest request, StreamObserver<Empty> responseObserver) {
        service.delete(request.getId())
            .doOnError(throwable -> responseObserver.onError(statusConverter.toGrpcException(throwable)))
            .doOnSuccess(response -> {
                responseObserver.onNext(Empty.getDefaultInstance());
                responseObserver.onCompleted();
            })
            .subscribe();
    }

    @Override
    public void findAllCustomers(FindAllCustomerRequest request, StreamObserver<CustomerResponse> responseObserver) {
        service.find(Pageable.unpaged())
            .doOnError(throwable -> responseObserver.onError(statusConverter.toGrpcException(throwable)))
            .map(customer ->
                CustomerResponse.newBuilder()
                    .setId(customer.id().toString())
                    .setEmail(customer.email())
                    .setName(customer.name())
                    .setPhone(customer.phone())
                    .setVat(customer.vat())
                    .build()
            )
            .map(response -> {
                responseObserver.onNext(response);
                return response;
            })
            .doOnComplete(responseObserver::onCompleted)
            .subscribe();
    }

    @Override
    public void findCustomerById(FindCustomerByIdRequest request, StreamObserver<CustomerResponse> responseObserver) {
        service.findById(request.getId())
            .doOnError(throwable -> responseObserver.onError(statusConverter.toGrpcException(throwable)))
            .map(customer ->
                CustomerResponse.newBuilder()
                    .setId(customer.id().toString())
                    .setEmail(customer.email())
                    .setName(customer.name())
                    .setPhone(customer.phone())
                    .setVat(customer.vat())
                    .build()
            )
            .map(response -> {
                responseObserver.onNext(response);
                return response;
            })
            .doOnSuccess(__ -> responseObserver.onCompleted())
            .subscribe();
    }

    @Override
    public void findCustomerByEmail(FindCustomerByEmailRequest request, StreamObserver<CustomerResponse> responseObserver) {
        service.findByEmail(request.getEmail())
            .doOnError(throwable -> responseObserver.onError(statusConverter.toGrpcException(throwable)))
            .map(customer ->
                CustomerResponse.newBuilder()
                    .setId(customer.id().toString())
                    .setEmail(customer.email())
                    .setName(customer.name())
                    .setPhone(customer.phone())
                    .setVat(customer.vat())
                    .build()
            )
            .map(response -> {
                responseObserver.onNext(response);
                return response;
            })
            .doOnSuccess(__ -> responseObserver.onCompleted())
            .subscribe();
    }

    @Override
    public void findCustomerByVat(FindCustomerByVatRequest request, StreamObserver<CustomerResponse> responseObserver) {
        service.findByVat(request.getVat())
            .doOnError(throwable -> responseObserver.onError(statusConverter.toGrpcException(throwable)))
            .map(customer ->
                CustomerResponse.newBuilder()
                    .setId(customer.id().toString())
                    .setEmail(customer.email())
                    .setName(customer.name())
                    .setPhone(customer.phone())
                    .setVat(customer.vat())
                    .build()
            )
            .map(response -> {
                responseObserver.onNext(response);
                return response;
            })
            .doOnSuccess(__ -> responseObserver.onCompleted())
            .subscribe();
    }
}