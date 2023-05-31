package com.kali.banksystem.accountservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String accountNumber;
    private BigDecimal balance;
    private boolean isActive;

    private String accountType;
    private String iban;
    private Date startDate;
    private Date endDate;
    private String accountName;

    private Long clientId; // Stores the ID of the associated client

    @Transient
    private List<Long> cardIds = new ArrayList<>(); // Stores the IDs of the associated cards


    // TODO: Event publishing method for creating account events


    //TODO: Event publishing method for updating account events

}
