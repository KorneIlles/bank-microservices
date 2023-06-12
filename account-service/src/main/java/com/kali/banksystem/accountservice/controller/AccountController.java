package com.kali.banksystem.accountservice.controller;


import com.kali.banksystem.accountservice.dto.account.AccountRequest;
import com.kali.banksystem.accountservice.dto.account.AccountResponse;
import com.kali.banksystem.accountservice.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompletableFuture<AccountResponse> registerClient(@RequestBody AccountRequest accountRequest){
         return accountService.createAccount(accountRequest);
    }

    @GetMapping("/{clientId}")
    @ResponseStatus(HttpStatus.OK)
    public List<AccountResponse> getAllAccountByClientId(@PathVariable Long clientId){
        return accountService.getAllAccountByClientId(clientId);
    }
}