package com.kali.banksystem.clientservice.model;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "client")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private Date dateOfBirth;

    private String address;

    private String phoneNumber;

    @Transient
    private List<Long> accountIds = new ArrayList<>();

    private Date createdAt;

    private Date updatedAt;

    private boolean isActive;


    public String getFullName() {
        return firstName +" "+lastName;
    }
}

