package com.prj.banking_system.controller;

import com.prj.banking_system.model.User;
import com.prj.banking_system.services.UserService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import com.prj.banking_system.repository.TransactionRepository;
import com.prj.banking_system.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller

public class PageController {

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionRepository transactionRepository;

    // methods below
    @GetMapping("/")
    public String homePage() {

        return "index";
    }

    // CREATE ACCOUNT PAGE

    @GetMapping("/create-account")
    public String createAccountPage() {

        return "create-account";
    }

    // DEPOSIT PAGE

    @GetMapping("/deposit")
    public String depositPage() {

        return "deposit";
    }

    // WITHDRAW PAGE

    @GetMapping("/withdraw")
    public String withdrawPage() {

        return "withdraw";
    }

    // BALANCE PAGE

    @GetMapping("/balance")
    public String balancePage() {

        return "balance";
    }

    // TRANSACTIONS PAGE

    @GetMapping("/transactions")
    public String transactionPage() {

        return "transactions";
    }

    // TRANSFER PAGE

    @GetMapping("/transfer")
    public String transferPage() {

        return "transfer";
    }

    // KYC PAGE

    @GetMapping("/kyc")
    public String kycPage() {

        return "kyc";
    }

    // ADMIN PAGE
@GetMapping("/admin")

public String adminPage() {

    return "admin";
}
//adminLogin
  @PostMapping("/adminLogin")

public String adminLogin(

        @RequestParam String adminId,

        @RequestParam String password,

        Model model) {

    if(adminId == null ||
       password == null ||
       adminId.isEmpty() ||
       password.isEmpty()) {

        model.addAttribute(
                "error",
                "Enter Admin ID and Password"
        );

        return "admin";
    }

    if(!adminId.equals("admin")) {

        model.addAttribute(
                "error",
                "Invalid Admin ID"
        );

        return "admin";
    }

    if(!password.equals("admin123")) {

        model.addAttribute(
                "error",
                "Incorrect Password"
        );

        return "admin";
    }

    return "admin-dashboard";
}
    // SAVE USER

    @PostMapping("/saveUser")

    public String saveUser(User user,
                           Model model) {

        String accountNumber;

        do {

            accountNumber =
                    String.valueOf(
                            1000000000L +
                            (long)(Math.random() * 9000000000L)
                    );

        } while(userService.accountExists(accountNumber));

        user.setAccountNumber(accountNumber);

        String phoneLast5 =
                user.getPhone()
                .substring(user.getPhone().length() - 5);

        String dobPassword =
                user.getDob()
                .replace("-", "");

        String finalPassword =
                phoneLast5 + dobPassword;

        user.setPassword(finalPassword);

        user.setBalance(0);
        user.setStatus("ACTIVE");
        user.setKycStatus("PENDING");
        userService.saveUser(user);

        model.addAttribute(
                "accountNumber",
                accountNumber
        );

        model.addAttribute(
                "password",
                finalPassword
        );

        return "success";
    }
    //depositeMoney
@PostMapping("/depositMoney")

public String depositMoney(

        @RequestParam String accountNumber,

        @RequestParam double amount,

        Model model) {

    User user =
            userService
            .getUserByAccountNumber(accountNumber);

    if(user == null) {

        model.addAttribute(
                "error",
                "Account not found"
        );

        return "deposit";
    }

    if(user.getStatus().equals("BLOCKED")) {

        model.addAttribute(
                "error",
                "Account is blocked"
        );

        return "deposit";
    }

    double newBalance =
            user.getBalance() + amount;

    user.setBalance(newBalance);

    userService.saveUser(user);

    userService.saveTransaction(

            accountNumber,

            null,

            "DEPOSIT",

            "CASH",

            amount,

            user.getBalance()
    );

    model.addAttribute(
            "amount",
            amount
    );

    return "deposit-success";
}
//checkBalance
@PostMapping("/checkBalance")

public String checkBalance(

        @RequestParam String accountNumber,

        @RequestParam String password,

        Model model) {

    User user =
            userService
            .getUserByAccountNumber(accountNumber);

    if(user == null) {

        model.addAttribute(
                "error",
                "Account not found"
        );

        return "balance";
    }

    if(!user.getPassword().equals(password)) {

        model.addAttribute(
                "error",
                "Incorrect password"
        );

        return "balance";
    }

    if(user.getStatus().equals("BLOCKED")) {

        model.addAttribute(
                "error",
                "Account is blocked"
        );

        return "balance";
    }

    model.addAttribute(
            "accountNumber",
            user.getAccountNumber()
    );

    model.addAttribute(
            "name",
            user.getName()
    );

    model.addAttribute(
            "balance",
            user.getBalance()
    );

    return "balance-success";
}
//transferMoney
  @PostMapping("/transferMoney")

public String transferMoney(

        @RequestParam String fromAccount,

        @RequestParam String password,

        @RequestParam String toAccount,

        @RequestParam double amount,

        Model model) {

    User sender =
            userService
            .getUserByAccountNumber(fromAccount);

    User receiver =
            userService
            .getUserByAccountNumber(toAccount);

    if(sender == null) {

        model.addAttribute(
                "error",
                "Sender account not found"
        );

        return "transfer";
    }

    if(receiver == null) {

        model.addAttribute(
                "error",
                "Receiver account not found"
        );

        return "transfer";
    }

    if(!sender.getPassword().equals(password)) {

        model.addAttribute(
                "error",
                "Incorrect password"
        );

        return "transfer";
    }

    if(sender.getStatus().equals("BLOCKED")) {

        model.addAttribute(
                "error",
                "Your account is blocked"
        );

        return "transfer";
    }

    if(receiver.getStatus().equals("BLOCKED")) {

        model.addAttribute(
                "error",
                "Receiver account is blocked"
        );

        return "transfer";
    }

    if(sender.getBalance() < amount) {

        model.addAttribute(
                "error",
                "Insufficient balance"
        );

        return "transfer";
    }

    sender.setBalance(
            sender.getBalance() - amount
    );

    receiver.setBalance(
            receiver.getBalance() + amount
    );

    userService.saveUser(sender);

    userService.saveUser(receiver);

    userService.saveTransaction(

            fromAccount,

            toAccount,

            "TRANSFER SENT",

            "ONLINE",

            amount,

            sender.getBalance()
    );

    userService.saveTransaction(

            toAccount,

            fromAccount,

            "TRANSFER RECEIVED",

            "ONLINE",

            amount,

            receiver.getBalance()
    );

    model.addAttribute(
            "amount",
            amount
    );

    model.addAttribute(
            "toAccount",
            toAccount
    );

    return "transfer-success";
}
//withdrawMoney
    @PostMapping("/withdrawMoney")

public String withdrawMoney(

        @RequestParam String accountNumber,

        @RequestParam String password,

        @RequestParam double amount,

        Model model) {

    User user =
            userService
            .getUserByAccountNumber(accountNumber);

    if(user == null) {

        model.addAttribute(
                "error",
                "Account not found"
        );

        return "withdraw";
    }

    if(!user.getPassword().equals(password)) {

        model.addAttribute(
                "error",
                "Incorrect password"
        );

        return "withdraw";
    }

    if(user.getStatus().equals("BLOCKED")) {

        model.addAttribute(
                "error",
                "Account is blocked"
        );

        return "withdraw";
    }

    if(user.getBalance() < amount) {

        model.addAttribute(
                "error",
                "Insufficient balance"
        );

        return "withdraw";
    }

    double newBalance =
            user.getBalance() - amount;

    user.setBalance(newBalance);

    userService.saveUser(user);

    userService.saveTransaction(

            accountNumber,

            null,

            "WITHDRAW",

            "CASH",

            amount,

            user.getBalance()
    );

    model.addAttribute(
            "amount",
            amount
    );

    return "withdraw-success";
}
//viewTransactions
@PostMapping("/viewTransactions")

public String viewTransactions(

        @RequestParam String accountNumber,

        @RequestParam String password,

        @RequestParam String fromDate,

        @RequestParam String toDate,

        Model model) {

    User user =
            userService
            .getUserByAccountNumber(accountNumber);

    if(user == null) {

        model.addAttribute(
                "error",
                "Account not found"
        );

        return "transactions";
    }

    if(!user.getPassword().equals(password)) {

        model.addAttribute(
                "error",
                "Incorrect password"
        );

        return "transactions";
    }

    if(user.getStatus().equals("BLOCKED")) {

        model.addAttribute(
                "error",
                "Account is blocked"
        );

        return "transactions";
    }

    LocalDateTime start =
            LocalDate
            .parse(fromDate)
            .atStartOfDay();

    LocalDateTime end =
            LocalDate
            .parse(toDate)
            .atTime(LocalTime.MAX);

    List<Transaction> transactions =
            userService
            .getTransactions(
                    accountNumber,
                    start,
                    end
            );

    model.addAttribute(
            "transactions",
            transactions
    );

    return "transaction-list";
}
//verifyKyc
@PostMapping("/verifyKyc")

public String verifyKyc(

        @RequestParam String accountNumber,

        @RequestParam String password,

        Model model) {

    User user =
            userService
            .getUserByAccountNumber(accountNumber);

    if(user == null) {

        model.addAttribute(
                "error",
                "Account not found"
        );

        return "kyc";
    }

    if(!user.getPassword().equals(password)) {

        model.addAttribute(
                "error",
                "Incorrect password"
        );

        return "kyc";
    }

    if(user.getStatus().equals("BLOCKED")) {

        model.addAttribute(
                "error",
                "Account is blocked"
        );

        return "kyc";
    }

    model.addAttribute(
            "user",
            user
    );

    return "update-kyc";
}
@PostMapping("/updateKyc")

public String updateKyc(

        User updatedUser,

        Model model) {

    User oldUser =
            userService
            .getUserByAccountNumber(
                    updatedUser.getAccountNumber()
            );

    if(oldUser != null) {

        oldUser.setName(
                updatedUser.getName()
        );

        oldUser.setEmail(
                updatedUser.getEmail()
        );

        oldUser.setPhone(
                updatedUser.getPhone()
        );

        oldUser.setDob(
                updatedUser.getDob()
        );

        oldUser.setAddress(
                updatedUser.getAddress()
        );

        userService.saveUser(oldUser);

        model.addAttribute(
                "name",
                oldUser.getName()
        );

        return "kyc-success";
    }

    return "kyc";
}
@GetMapping("/statistics")

public String statistics(Model model) {

    model.addAttribute(
            "totalUsers",
            userService.totalUsers()
    );

    model.addAttribute(
            "totalTransactions",
            transactionRepository.count()
    );

    model.addAttribute(
            "totalBalance",
            userService.totalBalance()
    );

    model.addAttribute(
            "activeAccounts",
            userService.activeAccounts()
    );

    model.addAttribute(
            "blockedAccounts",
            userService.blockedAccounts()
    );

    return "statistics";
}
@GetMapping("/allUsers")

public String allUsers(Model model) {

    model.addAttribute(
            "users",
            userService.getAllUsers()
    );

    return "all-users";
}
@GetMapping("/updateKycStatus")

public String updateKycStatus(

        @RequestParam String accountNumber) {

    User user =
            userService
            .getUserByAccountNumber(accountNumber);

    if(user.getKycStatus().equals("PENDING")) {

        user.setKycStatus("APPROVED");

    } else {

        user.setKycStatus("PENDING");
    }

    userService.saveUser(user);

    return "redirect:/allUsers";
}
@GetMapping("/toggleStatus")

public String toggleStatus(

        @RequestParam String accountNumber) {

    User user =
            userService
            .getUserByAccountNumber(accountNumber);

    if(user.getStatus().equals("ACTIVE")) {

        user.setStatus("BLOCKED");

    } else {

        user.setStatus("ACTIVE");
    }

    userService.saveUser(user);

    return "redirect:/allUsers";
}
@GetMapping("/adminTransactions")

public String adminTransactions(

        @RequestParam String accountNumber,

        Model model) {

    List<Transaction> transactions =
            transactionRepository
            .findByAccountNumber(accountNumber);

    model.addAttribute(
            "transactions",
            transactions
    );

    return "transaction-list";
}

}