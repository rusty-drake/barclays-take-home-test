package com.barclays.api.facade;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.BDDMockito.given;
import org.mockito.InOrder;
import org.mockito.Mock;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import org.mockito.junit.jupiter.MockitoExtension;

import com.barclays.api.domain.Account;
import com.barclays.api.domain.Address;
import com.barclays.api.domain.Transaction;
import com.barclays.api.domain.User;
import com.barclays.api.domain.enums.AccountType;
import com.barclays.api.domain.enums.Currency;
import com.barclays.api.domain.enums.TransactionType;
import com.barclays.api.services.AccountService;
import com.barclays.api.services.IdService;
import com.barclays.api.services.TransactionService;
import com.barclays.api.services.UserService;

@ExtendWith(MockitoExtension.class)
public class AccountsFacadeUnitTest {

    private AccountsFacade sut;

    @Mock
    private AccountService accountService;

    @Mock
    private UserService userService;

    @Mock
    private TransactionService transactionService;

    @Mock
    private IdService idService;

    @BeforeEach
    public void setup() {
        sut = new AccountsFacade(accountService, userService, transactionService, idService);
    }

    @Test
    public void createAccountWithBlankPrincipalEmailThrowsAnException() {
        // test fixtures
        final Account account = Account.Builder.create()
                .withName("Test Account")
                .withAccountType(AccountType.PERSONAL)
                .withBalance(BigDecimal.ZERO)
                .withCurrency(Currency.GBP)
                .build();

        // when/then - blank email
        assertThatThrownBy(() -> sut.createAccount(account, ""))
                .isInstanceOf(IllegalArgumentException.class);

        // when/then - whitespace email
        assertThatThrownBy(() -> sut.createAccount(account, "   "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void createAccountSuccessfullyCreatesAndReturnsNewAccount() {
        // test fixtures
        final String principalEmail = "test@example.com";
        final String sortCode = "10-10-10";
        final String accountNumber = "01234567";

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

        final Account newAccount = Account.Builder.create()
                .withName("Test Account")
                .withAccountType(AccountType.PERSONAL)
                .withBalance(BigDecimal.ZERO)
                .withCurrency(Currency.GBP)
                .build();

        final Account savedAccount = Account.Builder.create()
                .withName("Test Account")
                .withAccountType(AccountType.PERSONAL)
                .withBalance(BigDecimal.ZERO)
                .withCurrency(Currency.GBP)
                .withSortCode(sortCode)
                .withAccountNumber(accountNumber)
                .withUser(user)
                .build();
        savedAccount.setId(1L);

        // given
        given(accountService.getNewSortCode()).willReturn(sortCode);
        given(accountService.getNewAccountNumber()).willReturn(accountNumber);
        given(userService.findByEmail(eq(principalEmail))).willReturn(user);
        given(accountService.createAccount(any(Account.class))).willReturn(savedAccount);

        // when
        final Account result = sut.createAccount(newAccount, principalEmail);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isSameAs(savedAccount);
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getSortCode()).isEqualTo(sortCode);
        assertThat(result.getAccountNumber()).isEqualTo(accountNumber);
        assertThat(result.getUser()).isEqualTo(user);

        final InOrder verificationOrder = inOrder(accountService, userService);

        verificationOrder.verify(accountService, times(1)).getNewSortCode();
        verificationOrder.verify(accountService, times(1)).getNewAccountNumber();
        verificationOrder.verify(userService, times(1)).findByEmail(eq(principalEmail));
        verificationOrder.verify(accountService, times(1)).createAccount(same(newAccount));

        verifyNoMoreInteractions(accountService, userService);
    }

    @Test
    public void getAccountsWithBlankPrincipalEmailThrowsAnException() {
        // when/then - blank email
        assertThatThrownBy(() -> sut.getAccounts(""))
                .isInstanceOf(IllegalArgumentException.class);

        // when/then - whitespace email
        assertThatThrownBy(() -> sut.getAccounts("   "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void getAccountsSuccessfullyReturnsAccountsForPrincipal() {
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
        given(accountService.getAccounts(eq(principalEmail))).willReturn(expectedAccounts);

        // when
        final List<Account> result = sut.getAccounts(principalEmail);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyElementsOf(expectedAccounts);

        verify(accountService, times(1)).getAccounts(eq(principalEmail));
        verifyNoMoreInteractions(accountService, userService);
    }

    @Test
    public void getAccountWithBlankPrincipalEmailThrowsAnException() {
        // test fixtures
        final Long accountId = 1L;

        // when/then - blank email
        assertThatThrownBy(() -> sut.getAccount(accountId, ""))
                .isInstanceOf(IllegalArgumentException.class);

        // when/then - whitespace email
        assertThatThrownBy(() -> sut.getAccount(accountId, "   "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void getAccountSuccessfullyReturnsAccountWhenUserHasAccess() {
        // test fixtures
        final Long accountId = 1L;
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
        given(accountService.getById(eq(accountId))).willReturn(account);

        // when
        final Account result = sut.getAccount(accountId, principalEmail);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isSameAs(account);
        assertThat(result.getId()).isEqualTo(accountId);
        assertThat(result.getUser().getEmail()).isEqualTo(principalEmail);

        verify(accountService, times(1)).getById(eq(accountId));
        verifyNoMoreInteractions(accountService, userService);
    }

    @Test
    public void getAccountThrowsSecurityExceptionWhenUserDoesNotHaveAccess() {
        // test fixtures
        final Long accountId = 1L;
        final String principalEmail = "test@example.com";
        final String accountOwnerEmail = "different@example.com";

        final Address address = Address.Builder.create()
                .withLine1("123 Test Street")
                .withTown("Test Town")
                .withCounty("Test County")
                .withPostcode("TE1 2ST")
                .build();

        final User accountOwner = User.Builder.create()
                .withName("Account Owner")
                .withEmail(accountOwnerEmail)
                .withPhoneNumber("+441234567890")
                .withAddress(address)
                .build();
        accountOwner.setId(2L);

        final Account account = Account.Builder.create()
                .withName("Test Account")
                .withAccountType(AccountType.PERSONAL)
                .withBalance(BigDecimal.valueOf(100.00))
                .withCurrency(Currency.GBP)
                .withSortCode("10-10-10")
                .withAccountNumber("01234567")
                .withUser(accountOwner)
                .build();
        account.setId(accountId);

        // given
        given(accountService.getById(eq(accountId))).willReturn(account);

        // when
        try {
            sut.getAccount(accountId, principalEmail);
            
            // Should not reach here
            assertThat(false).as("getAccount() should have thrown an exception").isTrue();
        } catch (Exception ex) {
            // then
            assertThat(ex).isInstanceOf(SecurityException.class);

            final SecurityException securityException = (SecurityException) ex;
            assertThat(securityException.getMessage())
                    .contains("Authenticated user does not have access to this account.");

            verify(accountService, times(1)).getById(eq(accountId));
            verifyNoMoreInteractions(accountService, userService);
        }
    }

    @Test
    public void createTransactionSuccessfullyCreatesTransaction() {
        // test fixtures
        final Long accountId = 1L;
        final String principalEmail = "test@example.com";
        final String generatedTransactionId = "tan-xyz123";

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

        final Account account = Account.Builder.create()
                .withName("Test Account")
                .withAccountType(AccountType.PERSONAL)
                .withBalance(new BigDecimal("1000.00"))
                .withCurrency(Currency.GBP)
                .withUser(user)
                .build();
        account.setId(accountId);

        final Transaction transaction = Transaction.Builder.create()
                .withTransactionId("tan-test")
                .withAmount(new BigDecimal("100.00"))
                .withType(TransactionType.DEPOSIT)
                .withCurrency(Currency.GBP)
                .build();

        // given
        given(userService.findByEmail(eq(principalEmail))).willReturn(user);
        given(accountService.getById(eq(accountId))).willReturn(account);
        given(idService.generateId(eq("tan"))).willReturn(generatedTransactionId);
        given(transactionService.createTransaction(any(Transaction.class))).willReturn(transaction);

        // when
        sut.createTransaction(accountId, transaction, principalEmail);

        // then
        assertThat(transaction.getUser()).isEqualTo(user);
        assertThat(transaction.getAccount()).isEqualTo(account);
        assertThat(transaction.getId()).isEqualTo(generatedTransactionId);

        verify(userService, times(1)).findByEmail(principalEmail);
        verify(accountService, times(1)).getById(accountId);
        verify(idService, times(1)).generateId("tan");
        verify(transactionService, times(1)).createTransaction(transaction);
        verify(accountService, times(1)).updateBalance(account);
        verifyNoMoreInteractions(userService, accountService, transactionService, idService);
    }

    @Test
    public void createTransactionThrowsExceptionWhenUserEmailDoesNotMatch() {
        // test fixtures
        final Long accountId = 1L;
        final String principalEmail = "test@example.com";
        final String differentEmail = "different@example.com";

        final User user = User.Builder.create()
                .withName("Test User")
                .withEmail(differentEmail) // Different email
                .build();

        final Transaction transaction = Transaction.Builder.create()
                .withTransactionId("tan-test")
                .withAmount(new BigDecimal("100.00"))
                .withType(TransactionType.DEPOSIT)
                .withCurrency(Currency.GBP)
                .build();

        // given
        given(userService.findByEmail(eq(principalEmail))).willReturn(user);

        // when & then
        assertThatThrownBy(() -> sut.createTransaction(accountId, transaction, principalEmail))
                .isInstanceOf(SecurityException.class)
                .hasMessage("Authenticated user does not have access to this account.");

        verify(userService, times(1)).findByEmail(principalEmail);
        verifyNoMoreInteractions(userService, accountService, transactionService, idService);
    }

    @Test
    public void createTransactionThrowsExceptionWhenUserEmailIsNull() {
        // test fixtures
        final Long accountId = 1L;
        final String principalEmail = "test@example.com";

        final User user = User.Builder.create()
                .withName("Test User")
                .withEmail(null) // Null email
                .build();

        final Transaction transaction = Transaction.Builder.create()
                .withTransactionId("tan-test")
                .withAmount(new BigDecimal("100.00"))
                .withType(TransactionType.DEPOSIT)
                .withCurrency(Currency.GBP)
                .build();

        // given
        given(userService.findByEmail(eq(principalEmail))).willReturn(user);

        // when & then
        assertThatThrownBy(() -> sut.createTransaction(accountId, transaction, principalEmail))
                .isInstanceOf(SecurityException.class)
                .hasMessage("Authenticated user does not have access to this account.");

        verify(userService, times(1)).findByEmail(principalEmail);
        verifyNoMoreInteractions(userService, accountService, transactionService, idService);
    }

    @Test
    public void getTransactionsReturnsTransactionsWhenUserHasAccess() {
        // test fixtures
        final Long accountId = 1L;
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

        final Account account = Account.Builder.create()
                .withName("Test Account")
                .withAccountType(AccountType.PERSONAL)
                .withBalance(new BigDecimal("1000.00"))
                .withCurrency(Currency.GBP)
                .withUser(user)
                .build();
        account.setId(accountId);

        final List<Transaction> expectedTransactions = Arrays.asList(
                Transaction.Builder.create()
                        .withTransactionId("tan-1")
                        .withAmount(new BigDecimal("100.00"))
                        .withType(TransactionType.DEPOSIT)
                        .withCurrency(Currency.GBP)
                        .withAccount(account)
                        .build(),
                Transaction.Builder.create()
                        .withTransactionId("tan-2")
                        .withAmount(new BigDecimal("50.00"))
                        .withType(TransactionType.WITHDRAWAL)
                        .withCurrency(Currency.GBP)
                        .withAccount(account)
                        .build()
        );

        // given
        given(accountService.getById(eq(accountId))).willReturn(account);
        given(transactionService.getTransactionsByAccountId(eq(accountId))).willReturn(expectedTransactions);

        // when
        List<Transaction> result = sut.getTransactions(accountId, principalEmail);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(expectedTransactions);

        verify(accountService, times(1)).getById(accountId);
        verify(transactionService, times(1)).getTransactionsByAccountId(accountId);
        verifyNoMoreInteractions(accountService, transactionService, userService, idService);
    }

    @Test
    public void getTransactionsThrowsExceptionWhenUserDoesNotHaveAccess() {
        // test fixtures
        final Long accountId = 1L;
        final String principalEmail = "test@example.com";
        final String differentEmail = "different@example.com";

        final User differentUser = User.Builder.create()
                .withName("Different User")
                .withEmail(differentEmail)
                .build();

        final Account account = Account.Builder.create()
                .withName("Test Account")
                .withAccountType(AccountType.PERSONAL)
                .withBalance(new BigDecimal("1000.00"))
                .withCurrency(Currency.GBP)
                .withUser(differentUser) // Different user
                .build();
        account.setId(accountId);

        // given
        given(accountService.getById(eq(accountId))).willReturn(account);

        // when & then
        assertThatThrownBy(() -> sut.getTransactions(accountId, principalEmail))
                .isInstanceOf(SecurityException.class)
                .hasMessage("Authenticated user does not have access to this account.");

        verify(accountService, times(1)).getById(accountId);
        verifyNoMoreInteractions(accountService, transactionService, userService, idService);
    }
}
