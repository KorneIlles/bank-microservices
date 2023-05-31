package com.kali.banksystem.accountservice.dto.account;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountRequest {

    private String accountType;
    private String accountName;

    private Long clientId;
    private  String clientName;
}
