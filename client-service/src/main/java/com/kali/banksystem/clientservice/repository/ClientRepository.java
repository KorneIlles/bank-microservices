package com.kali.banksystem.clientservice.repository;

import com.kali.banksystem.clientservice.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    // Additional methods for custom queries or operations
}

