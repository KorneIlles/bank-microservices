package com.kali.banksystem.clientservice.dto.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Date dateOfBirth;
    private String address;
    private String phoneNumber;

    private List<Long> accountIds = new ArrayList<>();
    private Date createdAt;
    private Date updatedAt;
    private boolean isActive;
}
