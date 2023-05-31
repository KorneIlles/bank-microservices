package com.kali.banksystem.clientservice.controller;

import com.kali.banksystem.clientservice.dto.client.ClientRequest;
import com.kali.banksystem.clientservice.dto.client.ClientResponse;
import com.kali.banksystem.clientservice.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public void registerClient(@RequestBody ClientRequest clientRequest){
        clientService.registerClient(clientRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ClientResponse> getAllClient(){
        return clientService.getAllClient();
    }
}
