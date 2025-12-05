package com.barclays.api.services;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import org.mockito.junit.jupiter.MockitoExtension;

import com.barclays.api.dao.AccountDao;
import com.barclays.api.domain.Account;
import com.barclays.api.domain.Address;
import com.barclays.api.domain.User;
import com.barclays.api.domain.enums.AccountType;
import com.barclays.api.domain.enums.Currency;
import com.barclays.api.exceptions.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
public class AccountServiceUnitTest {

    private AccountService sut;

    @Mock
    private AccountDao accountDao;

    @BeforeEach
    public void setup() {
        sut = new AccountService(accountDao);
    }

    @Test
    public void getNewSortCodeReturnsValidFormat() {
        // when
        String sortCode = sut.getNewSortCode();

        // then
        assertThat(sortCode).isNotNull();
        assertThat(sortCode).matches("\\d{2}-\\d{2}-\\d{2}");
    }

    @Test
    public void getNewAccountNumberReturnsValidFormat() {
        // when
        String accountNumber = sut.getNewAccountNumber();

        // then
        assertThat(accountNumber).isNotNull();
        assertThat(accountNumber).matches("\\d{8}");
    }

    @Test
    public void createAccountSuccessfullyCreatesAndReturnsAccount() {
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

        final Account newAccount = Account.Builder.create()
                .withName("Test Account")
                .withAccountType(AccountType.PERSONAL)
                .withBalance(BigDecimal.ZERO)
                .withCurrency(Currency.GBP)
                .withSortCode("10-10-10")
                .withAccountNumber("01234567")
                .withUser(user)
                .build();

        final Account savedAccount = Account.Builder.create()
                .withName("Test Account")
                .withAccountType(AccountType.PERSONAL)
                .withBalance(BigDecimal.ZERO)
                .withCurrency(Currency.GBP)
                .withSortCode("10-10-10")
                .withAccountNumber("01234567")
                .withUser(user)
                .build();
        savedAccount.setId(1L);

        // given
        given(accountDao.save(any(Account.class))).willReturn(savedAccount);

        // when
        Account result = sut.createAccount(newAccount);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isSameAs(savedAccount);
        assertThat(result.getId()).isEqualTo(1L);

        verify(accountDao, times(1)).save(eq(newAccount));
        verifyNoMoreInteractions(accountDao);
    }

    @Test
    public void getAccountsSuccessfullyReturnsAccountsForUser() {
        // test fixtures
        final String principalEmail = "test@example.com";

        final Address address = Address.Builder.create()
                .withLine1("123 Test Street")
                .withTown("Test Town")
                .withCounty("Test County")
                .withPostcode("TE1 2ST")
                .build();

        final User user = User.Builder.create()
                .withName("Test User")
                .withEmail(principalEmail)
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

        final List<Account> expectedAccounts = Arrays.asList(account1, account2);

        // given
        given(accountDao.findByUserEmail(eq(principalEmail))).willReturn(expectedAccounts);

        // when
        List<Account> result = sut.getAccounts(principalEmail);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyElementsOf(expectedAccounts);

        verify(accountDao, times(1)).findByUserEmail(eq(principalEmail));
        verifyNoMoreInteractions(accountDao);
    }

    @Test
    public void getByIdSuccessfullyReturnsAccountWhenFound() {
        // test fixtures
        final Long accountId = 1L;

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
        account.setId(accountId);

        // given
        given(accountDao.findById(eq(accountId))).willReturn(Optional.of(account));

        // when
        Account result = sut.getById(accountId);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isSameAs(account);
        assertThat(result.getId()).isEqualTo(accountId);

        verify(accountDao, times(1)).findById(eq(accountId));
        verifyNoMoreInteractions(accountDao);
    }

    @Test
    public void getByIdThrowsResourceNotFoundExceptionWhenAccountNotFound() {
        // test fixtures
        final Long accountId = 999L;

        // given
        given(accountDao.findById(eq(accountId))).willReturn(Optional.empty());

        // when
        try {
            sut.getById(accountId);
            
            // Should not reach here
            assertThat(false).as("getById() should have thrown an exception").isTrue();
        } catch (Exception ex) {
            // then
            assertThat(ex).isInstanceOf(ResourceNotFoundException.class);

            final ResourceNotFoundException resourceNotFoundException = (ResourceNotFoundException) ex;
            assertThat(resourceNotFoundException.getMessage())
                    .contains("Account with ID " + accountId + " not found.");

            verify(accountDao, times(1)).findById(eq(accountId));
            verifyNoMoreInteractions(accountDao);
        }
    }
}
