package com.kali.banksystem.clientservice.service;


import com.kali.banksystem.clientservice.dto.account.AccountRequest;
import com.kali.banksystem.clientservice.dto.account.AccountResponse;
import com.kali.banksystem.clientservice.dto.client.ClientRequest;
import com.kali.banksystem.clientservice.dto.client.ClientResponse;
import com.kali.banksystem.clientservice.model.Client;
import com.kali.banksystem.clientservice.repository.ClientRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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


    public void registerClient(ClientRequest clientRequest) {
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


        //call the account service and create a base account
        clientRepository.save(client);
        log.info("client {} is registered", client.getId());

        AccountRequest accountRequest = getAccountRequest(client);
        Mono<AccountResponse> createdAccount = createAccount(accountRequest);

        createdAccount.subscribe(account -> {
            // Print the account details
            log.info("Created account: {}", account.getId());
        });

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
        return webClientBuilder.build().post()
                .uri("http://account-service/api/account")
                .bodyValue(accountRequest)
                .retrieve()
                .bodyToMono(AccountResponse.class);
    }
}
