package com.barclays.api.controllers;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.barclays.api.domain.Account;
import com.barclays.api.domain.Transaction;
import com.barclays.api.facade.AccountsFacade;

@RestController
@RequestMapping("/v1/accounts")
public class AccountsController {

    private final AccountsFacade accountFacade;

    @Autowired
    public AccountsController(
        AccountsFacade accountFacade
    ) {
        this.accountFacade = accountFacade;
    }

    @PostMapping
    public ResponseEntity<Account> createAccount(@Valid @RequestBody Account account, 
        Authentication authentication) {

        System.out.println("Account:" + account);
        String principalEmail = authentication.getName();
        Account createdAccount = accountFacade.createAccount(account, principalEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAccount);
    }

    @GetMapping
    public ResponseEntity<List<Account>> getAccounts(Authentication authentication) {

        String principalEmail = authentication.getName();
        List<Account> accounts = accountFacade.getAccounts(principalEmail);
        return ResponseEntity.status(HttpStatus.OK).body(accounts);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<Account> getAccount(@PathVariable Long accountId, Authentication authentication) {

        String principalEmail = authentication.getName();
        Account account = accountFacade.getAccount(accountId, principalEmail);
        return ResponseEntity.status(HttpStatus.OK).body(account);
    }

    @PostMapping("/{accountId}/transactions")
    public ResponseEntity<Void> createTransaction(@PathVariable Long accountId, @Valid @RequestBody Transaction transaction, 
        Authentication authentication) {

        String principalEmail = authentication.getName();
        accountFacade.createTransaction(accountId, transaction, principalEmail);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/{accountId}/transactions")
    public ResponseEntity<List<Transaction>> getTransactions(@PathVariable Long accountId, Authentication authentication) {
        String principalEmail = authentication.getName();
        List<Transaction> transactions = accountFacade.getTransactions(accountId, principalEmail);
        return ResponseEntity.status(HttpStatus.OK).body(transactions);
    }
}
