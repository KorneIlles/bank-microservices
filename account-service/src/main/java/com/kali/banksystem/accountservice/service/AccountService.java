package com.kali.banksystem.accountservice.service;


import com.kali.banksystem.accountservice.dto.account.AccountRequest;
import com.kali.banksystem.accountservice.dto.account.AccountResponse;
import com.kali.banksystem.accountservice.dto.card.CardRequest;
import com.kali.banksystem.accountservice.dto.card.CardResponse;
import com.kali.banksystem.accountservice.model.Account;
import com.kali.banksystem.accountservice.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;
//    private  final WebClient webClient;
    private  final WebClient.Builder webClientBuilder;

    @Autowired
    public AccountService(AccountRepository accountRepository, WebClient.Builder webClientBuilder) {
        this.accountRepository = accountRepository;
        this.webClientBuilder = webClientBuilder;
    }

    public void createAccount(AccountRequest accountRequest) {

        Account account = Account.builder()
                .accountNumber(generateRandomAccountNumber())
                .balance(BigDecimal.ZERO)
                .isActive(true)
                .accountType(accountRequest.getAccountType())
                .iban(createIbanNumber())
                .startDate(new Date())
                .endDate(null)
                .accountName(accountRequest.getAccountName())
                .clientId(accountRequest.getClientId())
                .cardIds(new ArrayList<>())
                .build();


        accountRepository.save(account);
        CardRequest cardRequest = getCardRequest(accountRequest.getClientName(), account.getId(), "standard");
        Mono<CardResponse> createdCard = createCard(cardRequest);
        createdCard.subscribe(card -> {
            log.info("created card {}", card.getId());
        });


    }

    public List<AccountResponse> getAllAccountByClientId(Long clientId) {
        List<Account> accounts = accountRepository.findAllByClientId(clientId);
        return accounts.stream().map(this::mapToAccountResponse).toList();
    }

    private AccountResponse mapToAccountResponse(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .accountName(account.getAccountName())
                .balance(account.getBalance())
                .isActive(account.isActive())
                .accountType(account.getAccountType())
                .iban(account.getIban())
                .startDate(account.getStartDate())
                .endDate(account.getEndDate())
                .accountName(account.getAccountName())
                .clientId(account.getClientId())
                .build();
    }

    private String createIbanNumber() {
        // Generate a random country code (e.g., "XX")
        String countryCode = generateRandomCountryCode();

        // Generate a random account number (e.g., "1234567890")
        String accountNumber = generateRandomAccountNumber();

        // Create the IBAN by concatenating the country code and account number
        String iban = countryCode + accountNumber;

        return iban;
    }

    private String generateRandomCountryCode() {
        // List of sample country codes (replace with actual country codes)
        String[] countryCodes = {
                "AD", "AE", "AF", "AG", "AI", "AL", "AM", "AO", "AQ", "AR", "AS", "AT", "AU", "AW",
                // Add more country codes...
        };

        // Select a random country code
        int randomIndex = (int) (Math.random() * countryCodes.length);
        return countryCodes[randomIndex];
    }

    private String generateRandomAccountNumber() {
        // Generate a random 10-digit account number
        Random random = new Random();
        long accountNumber = random.nextLong();
        accountNumber = Math.abs(accountNumber % 1_000_000_000); // Limit to 10 digits

        return String.format("%010d", accountNumber);
    }

    private Mono<CardResponse> createCard(CardRequest cardRequest) {

        return webClientBuilder.build().post()
                .uri("http://card-service/api/card")
                .bodyValue(cardRequest)
                .retrieve()
                .bodyToMono(CardResponse.class);
    }

    private CardRequest getCardRequest(String clientName, Long accountId, String cardType) {
        return CardRequest.builder()
                .cardType(cardType)
                .accountId(accountId)
                .cardHolderName(clientName)
                .build();
    }


}
