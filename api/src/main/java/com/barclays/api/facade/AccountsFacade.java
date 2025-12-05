package com.barclays.api.facade;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.barclays.api.domain.Account;
import com.barclays.api.domain.User;
import com.barclays.api.services.AccountService;
import com.barclays.api.services.UserService;

@Component
// @Validated
public class AccountsFacade {

    private final AccountService accountService;
    private final UserService userService;

    @Autowired
    public AccountsFacade(
        AccountService accountService,
        UserService userService
    ) {
        this.accountService = accountService;
        this.userService = userService;
    }

    public Account createAccount(@NotNull Account account, @NotBlank String principalEmail) {
        String sortCode = accountService.getNewSortCode();
        account.setSortCode(sortCode);

        String accountNumber =  accountService.getNewAccountNumber();
        account.setAccountNumber(accountNumber);

        User user = userService.findByEmail(principalEmail);
        account.setUser(user);
        account = accountService.createAccount(account);

        return account;
    }

    public List<Account> getAccounts(@NotBlank String principalEmail) {
        return accountService.getAccounts(principalEmail);
    }

    public Account getAccount(@NotNull Long accountId, @NotBlank String principalEmail) {
        Account account = accountService.getById(accountId);
        if (!account.getUser().getEmail().equals(principalEmail)) {
            throw new SecurityException("Authenticated user does not have access to this account.");
        }

        return account;
    }

}
