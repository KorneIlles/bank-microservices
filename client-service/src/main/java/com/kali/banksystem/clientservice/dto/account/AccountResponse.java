package com.kali.banksystem.clientservice.dto.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountResponse {

    private Long id;

    private String accountNumber;
    private BigDecimal balance;
    private boolean isActive;

    private String accountType;
    private String iban;
    private Date startDate;
    private Date endDate;
    private String accountName;
    private Long clientId;

}