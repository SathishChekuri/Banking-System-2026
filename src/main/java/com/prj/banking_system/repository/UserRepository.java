package com.prj.banking_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prj.banking_system.model.User;

public interface UserRepository
        extends JpaRepository<User, Long> {
      long countByStatus(String status);
    boolean existsByAccountNumber(String accountNumber);

    User findByAccountNumber(String accountNumber);
}