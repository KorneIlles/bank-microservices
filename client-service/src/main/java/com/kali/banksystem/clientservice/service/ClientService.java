package com.kali.banksystem.clientservice.service;


import com.kali.banksystem.clientservice.dto.account.AccountRequest;
import com.kali.banksystem.clientservice.dto.account.AccountResponse;
import com.kali.banksystem.clientservice.dto.client.ClientRequest;
import com.kali.banksystem.clientservice.dto.client.ClientResponse;
import com.kali.banksystem.clientservice.model.Client;
import com.kali.banksystem.clientservice.repository.ClientRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class ClientService {

    private final ClientRepository clientRepository;
    private final WebClient.Builder webClientBuilder;

    @Autowired
    public ClientService(ClientRepository clientRepository, WebClient.Builder webClientBuilder) {
        this.clientRepository = clientRepository;
        this.webClientBuilder = webClientBuilder;
    }

    @CircuitBreaker(name = "account",fallbackMethod = "fallBackMethod")
    @TimeLimiter(name = "account")
    @Retry(name = "account")
    @Transactional
    public CompletableFuture<String> registerClient(ClientRequest clientRequest) {
        Client client = Client.builder()
                .firstName(clientRequest.getFirstName())
                .lastName(clientRequest.getLastName())
                .email(clientRequest.getEmail())
                .password(clientRequest.getPassword())
                .dateOfBirth(clientRequest.getDateOfBirth())
                .address(clientRequest.getAddress())
                .phoneNumber(clientRequest.getPhoneNumber())
                .createdAt(new Date())
                .updatedAt(new Date())
                .isActive(true)
                .accountIds(new ArrayList<>())
                .build();

        try {
            // Save the client entity
            clientRepository.save(client);
            log.info("Client {} is registered", client.getId());

            AccountRequest accountRequest = getAccountRequest(client);
            Mono<AccountResponse> createdAccount = createAccount(accountRequest);

            AccountResponse account = createdAccount.block(); // Wait for the Mono to complete and get the result
            log.info("base account {} is created for client id {} ", account.getId(), client.getId());
            return  CompletableFuture.supplyAsync(() ->"You register successfully!");

        } catch (Exception e) {
            // Handle the exception
            log.error("Failed to create account for client {}", client.getId(), e);
            throw e;
        }
    }


    private AccountRequest getAccountRequest(Client client) {
        return AccountRequest.builder()
                .clientId(client.getId())
                .accountName(client.getFirstName() + " " + client.getLastName() + "'s base account")
                .accountType("base account")
                .clientName(client.getFullName())
                .build();
    }

    public List<ClientResponse> getAllClient() {
        List<Client> clients = clientRepository.findAll();

        return clients.stream().map(this::mapToClientResponse).toList();
    }

    private ClientResponse mapToClientResponse(Client client) {
        return ClientResponse.builder()
                .id(client.getId())
                .firstName(client.getFirstName())
                .lastName(client.getLastName())
                .email(client.getEmail())
                .password(client.getPassword())
                .phoneNumber(client.getPhoneNumber())
                .accountIds(client.getAccountIds())
                .address(client.getAddress())
                .dateOfBirth(client.getDateOfBirth())
                .createdAt(client.getCreatedAt())
                .updatedAt(client.getUpdatedAt())
                .isActive(client.isActive())
                .build();
    }

    private Mono<AccountResponse> createAccount(AccountRequest accountRequest) {
        try {
            return webClientBuilder.build().post()
                    .uri("http://account-service/api/account")
                    .bodyValue(accountRequest)
                    .retrieve()
                    .bodyToMono(AccountResponse.class);
        } catch (Exception e) {
            // Handle or rethrow the exception
            log.error("Failed to create account", e);
            throw e;
        }
    }

    private CompletableFuture<String>  fallBackMethod(ClientRequest clientRequest, RuntimeException runtimeException){
        return CompletableFuture.supplyAsync(() ->"Oops! Something went wrong, please try later!");
    }

}
