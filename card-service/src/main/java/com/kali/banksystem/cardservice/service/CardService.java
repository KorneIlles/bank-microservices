package com.kali.banksystem.cardservice.service;


import com.kali.banksystem.cardservice.dto.CardRequest;
import com.kali.banksystem.cardservice.dto.CardResponse;
import com.kali.banksystem.cardservice.model.Card;
import com.kali.banksystem.cardservice.repository.CardRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Service
@Slf4j
public class CardService {

    private final CardRepository cardRepository;

    @Autowired
    public CardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }


    public CardResponse createCard(CardRequest cardRequest) throws Exception {
        Card card = Card.builder()
                .cardType(cardRequest.getCardType())
                .pan(generatePAN())
                .accountId(cardRequest.getAccountId())
                .bankName("bank")
                .cardholderName(cardRequest.getCardHolderName())
                .cvv(createCCV())
                .build();

        try {
            cardRepository.save(card);
            log.info("card {} is registered", card.getId());
            return mapToCardResponse(card);
        }catch (Exception e){
            log.error("Failed to create card to this account {}", cardRequest.getAccountId(), e);
            cardRepository.delete(card);
            throw  new Exception("Failed to create card.");
        }
    }

    public List<CardResponse> getCardsByAccountId(Long accountId) {
        List<Card> cards = cardRepository.findAllByAccountId(accountId);

        return cards.stream().map(this::mapToCardResponse).toList();
    }

    private CardResponse mapToCardResponse(Card card) {
        return CardResponse.builder()
                .cardType(card.getCardType())
                .accountId(card.getAccountId())
                .bankName(card.getBankName())
                .cardholderName(card.getCardholderName())
                .cvv(card.getCvv())
                .id(card.getId())
                .pan(card.getPan())
                .build();
    }

    private String createCCV() {
        Random random = new Random();
        int ccv = random.nextInt(900) + 100; // Generates a random number between 100 and 999
        return String.valueOf(ccv);
    }


    public String generatePAN() {
        Random random = new Random();
        StringBuilder panBuilder = new StringBuilder();

        // Generate the first few digits (Issuer Identification Number)
        int iin = getRandomInRange(4000, 4999); // Example range for demonstration
        panBuilder.append(iin);

        // Generate the remaining digits
        int digitCount = 16 - String.valueOf(iin).length() - 1; // Subtract 1 for the checksum digit
        for (int i = 0; i < digitCount; i++) {
            int digit = random.nextInt(10);
            panBuilder.append(digit);
        }

        int checksum = calculateLuhnChecksum(panBuilder.toString());
        panBuilder.append(checksum);

        return panBuilder.toString();
    }

    private int getRandomInRange(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }

    private int calculateLuhnChecksum(String pan) {
        int sum = 0;
        boolean doubleDigit = false;

        for (int i = pan.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(pan.charAt(i));

            if (doubleDigit) {
                digit *= 2;
                if (digit > 9) {
                    digit = digit - 9;
                }
            }

            sum += digit;
            doubleDigit = !doubleDigit;
        }

        int checksum = (sum * 9) % 10;
        return checksum;
    }
}
