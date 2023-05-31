package com.kali.banksystem.cardservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CardResponse {

    private Long id;
    private String cardholderName;
    private String pan;
    private String bankName;
    private String cardType;
    private String cvv;
    private Long accountId;
}
