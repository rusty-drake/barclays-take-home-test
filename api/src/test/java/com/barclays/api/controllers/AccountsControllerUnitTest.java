package com.barclays.api.controllers;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.barclays.api.domain.Account;
import com.barclays.api.domain.Address;
import com.barclays.api.domain.User;
import com.barclays.api.domain.enums.AccountType;
import com.barclays.api.domain.enums.Currency;
import com.barclays.api.facade.AccountsFacade;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(AccountsController.class)
public class AccountsControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AccountsFacade accountsFacade;

    @Test
    public void createAccountReturnsCreatedAccountWithStatus201() throws Exception {
        // test fixtures
        final Address address = Address.Builder.create()
                .withLine1("123 Test Street")
                .withTown("Test Town")
                .withCounty("Test County")
                .withPostcode("TE1 2ST")
                .build();

        final User user = User.Builder.create()
                .withName("Test User")
                .withEmail("test@example.com")
                .withPhoneNumber("+441234567890")
                .withAddress(address)
                .build();
        user.setId(1L);

        final Account inputAccount = Account.Builder.create()
                .withName("Test Account")
                .withAccountType(AccountType.PERSONAL)
                .withBalance(BigDecimal.ZERO)
                .withCurrency(Currency.GBP)
                .build();

        final Account createdAccount = Account.Builder.create()
                .withName("Test Account")
                .withAccountType(AccountType.PERSONAL)
                .withBalance(BigDecimal.ZERO)
                .withCurrency(Currency.GBP)
                .withSortCode("10-10-10")
                .withAccountNumber("01234567")
                .withUser(user)
                .build();
        createdAccount.setId(1L);

        // given
        given(accountsFacade.createAccount(any(Account.class), eq("test@example.com")))
                .willReturn(createdAccount);

        // when & then
        mockMvc.perform(post("/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputAccount)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Account"))
                .andExpect(jsonPath("$.accountType").value("personal"))
                .andExpect(jsonPath("$.balance").value(0.00))
                .andExpect(jsonPath("$.currency").value("GBP"))
                .andExpect(jsonPath("$.sortCode").value("10-10-10"))
                .andExpect(jsonPath("$.accountNumber").value("01234567"));
    }

    @Test
    public void createAccountWithInvalidDataReturnsBadRequest() throws Exception {
        // test fixtures - account with missing required fields
        final Account invalidAccount = Account.Builder.create()
                .withBalance(BigDecimal.ZERO)
                .withCurrency(Currency.GBP)
                .build(); // Missing name and accountType

        // when & then
        mockMvc.perform(post("/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidAccount)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getAccountsReturnsAccountsListWithStatus200() throws Exception {
        // test fixtures
        final Address address = Address.Builder.create()
                .withLine1("123 Test Street")
                .withTown("Test Town")
                .withCounty("Test County")
                .withPostcode("TE1 2ST")
                .build();

        final User user = User.Builder.create()
                .withName("Test User")
                .withEmail("test@example.com")
                .withPhoneNumber("+441234567890")
                .withAddress(address)
                .build();
        user.setId(1L);

        final Account account1 = Account.Builder.create()
                .withName("Test Account 1")
                .withAccountType(AccountType.PERSONAL)
                .withBalance(BigDecimal.valueOf(100.00))
                .withCurrency(Currency.GBP)
                .withSortCode("10-10-10")
                .withAccountNumber("01234567")
                .withUser(user)
                .build();
        account1.setId(1L);

        final Account account2 = Account.Builder.create()
                .withName("Test Account 2")
                .withAccountType(AccountType.PERSONAL)
                .withBalance(BigDecimal.valueOf(250.50))
                .withCurrency(Currency.GBP)
                .withSortCode("10-10-10")
                .withAccountNumber("01234568")
                .withUser(user)
                .build();
        account2.setId(2L);

        final List<Account> accounts = Arrays.asList(account1, account2);

        // given
        given(accountsFacade.getAccounts(eq("test@example.com")))
                .willReturn(accounts);

        // when & then
        mockMvc.perform(get("/v1/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Test Account 1"))
                .andExpect(jsonPath("$[0].accountType").value("personal"))
                .andExpect(jsonPath("$[0].balance").value(100.00))
                .andExpect(jsonPath("$[0].currency").value("GBP"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Test Account 2"))
                .andExpect(jsonPath("$[1].accountType").value("personal"))
                .andExpect(jsonPath("$[1].balance").value(250.50));
    }

    @Test
    public void getAccountByIdReturnsAccountWhenFoundWithStatus200() throws Exception {
        // test fixtures
        final Address address = Address.Builder.create()
                .withLine1("123 Test Street")
                .withTown("Test Town")
                .withCounty("Test County")
                .withPostcode("TE1 2ST")
                .build();

        final User user = User.Builder.create()
                .withName("Test User")
                .withEmail("test@example.com")
                .withPhoneNumber("+441234567890")
                .withAddress(address)
                .build();
        user.setId(1L);

        final Account account = Account.Builder.create()
                .withName("Test Account")
                .withAccountType(AccountType.PERSONAL)
                .withBalance(BigDecimal.valueOf(100.00))
                .withCurrency(Currency.GBP)
                .withSortCode("10-10-10")
                .withAccountNumber("01234567")
                .withUser(user)
                .build();
        account.setId(1L);

        final List<Account> accounts = Arrays.asList(account);

        // given
        given(accountsFacade.getAccounts(eq("test@example.com")))
                .willReturn(accounts);

        // when & then
        mockMvc.perform(get("/v1/accounts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Account"))
                .andExpect(jsonPath("$.accountType").value("personal"))
                .andExpect(jsonPath("$.balance").value(100.00))
                .andExpect(jsonPath("$.currency").value("GBP"))
                .andExpect(jsonPath("$.sortCode").value("10-10-10"))
                .andExpect(jsonPath("$.accountNumber").value("01234567"));
    }

    @Test
    public void getAccountByIdReturnsNotFoundWhenAccountDoesNotExist() throws Exception {
        // test fixtures
        final List<Account> emptyAccounts = Arrays.asList();

        // given
        given(accountsFacade.getAccounts(eq("test@example.com")))
                .willReturn(emptyAccounts);

        // when & then
        mockMvc.perform(get("/v1/accounts/999"))
                .andExpect(status().isNotFound());
    }
}
