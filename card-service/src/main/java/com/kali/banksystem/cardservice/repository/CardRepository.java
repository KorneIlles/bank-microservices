package com.kali.banksystem.cardservice.repository;

import com.kali.banksystem.cardservice.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card,Long> {

    List<Card> findAllByAccountId(Long accountId);
}
