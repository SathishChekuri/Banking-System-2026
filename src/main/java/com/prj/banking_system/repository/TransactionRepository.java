package com.prj.banking_system.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prj.banking_system.model.Transaction;

public interface TransactionRepository
        extends JpaRepository<Transaction, Long> {

    List<Transaction>
    findByAccountNumberAndTransactionDateBetween(

            String accountNumber,

            LocalDateTime start,

            LocalDateTime end
    );

    List<Transaction>
    findByAccountNumber(

            String accountNumber
    );
}