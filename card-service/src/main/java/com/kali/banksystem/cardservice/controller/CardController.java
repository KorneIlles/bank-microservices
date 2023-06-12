package com.kali.banksystem.cardservice.controller;


import com.kali.banksystem.cardservice.dto.CardRequest;
import com.kali.banksystem.cardservice.dto.CardResponse;
import com.kali.banksystem.cardservice.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/card")
public class CardController {

    private final CardService cardService;

    @Autowired
    public CardController(CardService cardService) {
        this.cardService = cardService;
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CardResponse createCard(@RequestBody CardRequest cardRequest) throws Exception {
        return cardService.createCard(cardRequest);
    }

    @GetMapping("/{accountId}")
    @ResponseStatus(HttpStatus.OK)
    public List<CardResponse> getAllCardByAccountId(@PathVariable Long accountId){
        return cardService.getCardsByAccountId(accountId);
    }

}
