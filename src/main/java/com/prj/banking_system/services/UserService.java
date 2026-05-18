package com.prj.banking_system.services;
import com.prj.banking_system.model.Transaction;
import com.prj.banking_system.repository.TransactionRepository;

import java.time.LocalDateTime;
import com.prj.banking_system.model.User;
import com.prj.banking_system.repository.UserRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service

public class UserService {
    @Autowired
     private TransactionRepository transactionRepository;
     public void saveTransaction(

        String accountNumber,

        String toAccount,

        String type,

        String method,

        double amount,

        double balance) {

    Transaction transaction =
            new Transaction();

    transaction.setAccountNumber(accountNumber);

    transaction.setToAccount(toAccount);

    transaction.setTransactionType(type);

    transaction.setMethod(method);

    transaction.setAmount(amount);

    transaction.setBalance(balance);

    transaction.setTransactionDate(
            LocalDateTime.now()
    );

    transactionRepository.save(transaction);
}
    @Autowired
    private UserRepository userRepository;

    public void saveUser(User user) {

        userRepository.save(user);
    }

    public boolean accountExists(String accountNumber) {

        return userRepository
                .existsByAccountNumber(accountNumber);
    }
    public User getUserByAccountNumber(String accountNumber) {

    return userRepository
            .findByAccountNumber(accountNumber);
   }
   public List<Transaction> getTransactions(
        String accountNumber,
        LocalDateTime start,
        LocalDateTime end) {

    return transactionRepository
            .findByAccountNumberAndTransactionDateBetween(
                    accountNumber,
                    start,
                    end
            );
}
public long totalUsers() {

    return userRepository.count();
}

public long activeAccounts() {

    return userRepository
            .countByStatus("ACTIVE");
}

public long blockedAccounts() {

    return userRepository
            .countByStatus("BLOCKED");
}

public double totalBalance() {

    return userRepository
            .findAll()
            .stream()
            .mapToDouble(User::getBalance)
            .sum();
}

public List<User> getAllUsers() {

    return userRepository.findAll();
}
}