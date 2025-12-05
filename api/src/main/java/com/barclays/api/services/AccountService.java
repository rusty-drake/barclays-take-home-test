package com.barclays.api.services;

import java.util.List;
import java.util.Random;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.barclays.api.dao.AccountDao;
import com.barclays.api.domain.Account;
import com.barclays.api.exceptions.ResourceNotFoundException;

@Service
@Validated
public class AccountService {

    private final AccountDao accountDao;

    @Autowired
    public AccountService(
        AccountDao accountDao
    ) {
        this.accountDao = accountDao;
    }
    
    public String getNewSortCode() {
        Random random = new Random();
        return String.format("%02d-%02d-%02d", random.nextInt(100), random.nextInt(100), random.nextInt(100));
    }

    public String getNewAccountNumber() {
        Random random = new Random();
        return String.format("%08d", random.nextInt(100000000));
    }

    public Account createAccount(@NotNull Account account) {
        System.out.println("Creating account: " + account);
        return accountDao.save(account);
    }

    public List<Account> getAccounts(@NotNull String principalEmail) {
        return accountDao.findByUserEmail(principalEmail);
    }

    public Account getById(@NotNull Long accountId) {

        Account account = accountDao.findById(accountId).orElse(null);
        if (account == null) {
            throw new ResourceNotFoundException("Account with ID " + accountId + " not found.");
        }
        return account;
    }

    public void updateBalance(@NotNull Account account) {
        accountDao.updateBalance(account.getId(), account.getBalance());
    }
}
