package com.kali.banksystem.accountservice.service;


import com.kali.banksystem.accountservice.dto.account.AccountRequest;
import com.kali.banksystem.accountservice.dto.account.AccountResponse;
import com.kali.banksystem.accountservice.dto.card.CardRequest;
import com.kali.banksystem.accountservice.dto.card.CardResponse;
import com.kali.banksystem.accountservice.model.Account;
import com.kali.banksystem.accountservice.repository.AccountRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;
    //    private  final WebClient webClient;
    private final WebClient.Builder webClientBuilder;

    @Autowired
    public AccountService(AccountRepository accountRepository, WebClient.Builder webClientBuilder) {
        this.accountRepository = accountRepository;
        this.webClientBuilder = webClientBuilder;
    }

    @CircuitBreaker(name = "card", fallbackMethod = "fallBackMethod")
    @TimeLimiter(name = "card")
    @Retry(name = "card")
    public CompletableFuture<String> createAccount(AccountRequest accountRequest) {
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

        log.info("{}",account);

        try {
            // Save the client entity
            accountRepository.save(account);
            log.info("Account {} is registered", account.getId());

            CardRequest cardRequest = getCardRequest(accountRequest.getClientName(), account.getId(), "standard");
            Mono<CardResponse> createdCard = createCard(cardRequest);

            CardResponse card = createdCard.block(); // Wait for the Mono to complete and get the result
            log.info("base card {} is created for account id {} ", card.getId(), account.getId());

            return CompletableFuture.supplyAsync(() -> String.format("base account %s is created for client id %s", account.getId(), account.getClientId()));
        } catch (Exception e) {
            // Handle the exception
            log.error("Failed to create card for account {}", account.getId(), e);
            accountRepository.delete(account);
            throw e;
        }
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
        log.info("CREATE CARD METGHOD --------------------------------------------------");
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

    private CompletableFuture<String> fallBackMethod(AccountRequest accountRequest, RuntimeException runtimeException) {
        return CompletableFuture.supplyAsync(() -> "Oops! Something went wrong, please try later!");
    }


}
