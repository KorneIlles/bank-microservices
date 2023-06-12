package com.kali.banksystem.clientservice.controller;

import com.kali.banksystem.clientservice.dto.client.ClientRequest;
import com.kali.banksystem.clientservice.dto.client.ClientResponse;
import com.kali.banksystem.clientservice.service.ClientService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/client")
public class ClientController {

    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompletableFuture<String> registerClient(@RequestBody ClientRequest clientRequest){
        return clientService.registerClient(clientRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ClientResponse> getAllClient(){
        return clientService.getAllClient();
    }


}
