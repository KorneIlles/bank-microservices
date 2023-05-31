package com.kali.banksystem.clientservice.dto.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Date dateOfBirth;
    private String address;
    private String phoneNumber;
}