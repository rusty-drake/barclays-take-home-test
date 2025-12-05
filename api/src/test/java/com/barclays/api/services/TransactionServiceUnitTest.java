package com.barclays.api.services;

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
import static org.mockito.BDDMockito.given;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import org.mockito.junit.jupiter.MockitoExtension;

import com.barclays.api.dao.TransactionDao;
import com.barclays.api.domain.Account;
import com.barclays.api.domain.Address;
import com.barclays.api.domain.Transaction;
import com.barclays.api.domain.User;
import com.barclays.api.domain.enums.AccountType;
import com.barclays.api.domain.enums.Currency;
import com.barclays.api.domain.enums.TransactionType;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceUnitTest {

    private TransactionService sut;

    @Mock
    private TransactionDao transactionDao;

    private User testUser;
    private Account testAccount;

    @BeforeEach
    public void setup() {
        sut = new TransactionService(transactionDao);
        
        // Create test fixtures
        final Address address = Address.Builder.create()
                .withLine1("123 Test Street")
                .withTown("Test Town")
                .withCounty("Test County")
                .withPostcode("TE1 2ST")
                .build();
        
        testUser = User.Builder.create()
                .withName("John Doe")
                .withEmail("john.doe@example.com")
                .withPhoneNumber("1234567890")
                .withAddress(address)
                .build();
        
        testAccount = Account.Builder.create()
                .withName("Test Personal Account")
                .withAccountType(AccountType.PERSONAL)
                .withBalance(new BigDecimal("1000.00"))
                .withCurrency(Currency.GBP)
                .withUser(testUser)
                .build();
        testAccount.setId(1L);
        testAccount.setAccountNumber("01234567");
        testAccount.setSortCode("10-10-10");
    }

    @Test
    public void createTransactionWithDepositIncreasesBalance() {
        // given
        Transaction depositTransaction = Transaction.Builder.create()
                .withTransactionId("tan-deposit1")
                .withAmount(new BigDecimal("100.00"))
                .withType(TransactionType.DEPOSIT)
                .withCurrency(Currency.GBP)
                .withAccount(testAccount)
                .build();

        given(transactionDao.save(any(Transaction.class))).willReturn(depositTransaction);

        // when
        Transaction result = sut.createTransaction(depositTransaction);

        // then
        assertThat(result).isNotNull();
        assertThat(testAccount.getBalance()).isEqualTo(new BigDecimal("1100.00"));
        verify(transactionDao, times(1)).save(depositTransaction);
        verifyNoMoreInteractions(transactionDao);
    }

    @Test
    public void createTransactionWithWithdrawalDecreasesBalance() {
        // given
        Transaction withdrawalTransaction = Transaction.Builder.create()
                .withTransactionId("tan-withdrawal1")
                .withAmount(new BigDecimal("200.00"))
                .withType(TransactionType.WITHDRAWAL)
                .withCurrency(Currency.GBP)
                .withAccount(testAccount)
                .build();

        given(transactionDao.save(any(Transaction.class))).willReturn(withdrawalTransaction);

        // when
        Transaction result = sut.createTransaction(withdrawalTransaction);

        // then
        assertThat(result).isNotNull();
        assertThat(testAccount.getBalance()).isEqualTo(new BigDecimal("800.00"));
        verify(transactionDao, times(1)).save(withdrawalTransaction);
        verifyNoMoreInteractions(transactionDao);
    }

    @Test
    public void createTransactionWithWithdrawalThrowsExceptionWhenInsufficientFunds() {
        // given
        Transaction withdrawalTransaction = Transaction.Builder.create()
                .withTransactionId("tan-withdrawal2")
                .withAmount(new BigDecimal("1500.00")) // More than available balance
                .withType(TransactionType.WITHDRAWAL)
                .withCurrency(Currency.GBP)
                .withAccount(testAccount)
                .build();

        // when & then
        assertThatThrownBy(() -> sut.createTransaction(withdrawalTransaction))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Insufficient funds for withdrawal.");
        
        // Verify balance remains unchanged
        assertThat(testAccount.getBalance()).isEqualTo(new BigDecimal("1000.00"));
        verify(transactionDao, times(0)).save(any(Transaction.class));
        verifyNoMoreInteractions(transactionDao);
    }

    @Test
    public void createTransactionWithWithdrawalExactBalanceSucceeds() {
        // given
        Transaction withdrawalTransaction = Transaction.Builder.create()
                .withTransactionId("tan-withdrawal3")
                .withAmount(new BigDecimal("1000.00")) // Exact balance
                .withType(TransactionType.WITHDRAWAL)
                .withCurrency(Currency.GBP)
                .withAccount(testAccount)
                .build();

        given(transactionDao.save(any(Transaction.class))).willReturn(withdrawalTransaction);

        // when
        Transaction result = sut.createTransaction(withdrawalTransaction);

        // then
        assertThat(result).isNotNull();
        assertThat(testAccount.getBalance()).isEqualTo(new BigDecimal("0.00"));
        verify(transactionDao, times(1)).save(withdrawalTransaction);
        verifyNoMoreInteractions(transactionDao);
    }

    @Test
    public void createTransactionWithZeroAmountDepositSucceeds() {
        // given
        Transaction depositTransaction = Transaction.Builder.create()
                .withTransactionId("tan-deposit2")
                .withAmount(BigDecimal.ZERO)
                .withType(TransactionType.DEPOSIT)
                .withCurrency(Currency.GBP)
                .withAccount(testAccount)
                .build();

        given(transactionDao.save(any(Transaction.class))).willReturn(depositTransaction);

        // when
        Transaction result = sut.createTransaction(depositTransaction);

        // then
        assertThat(result).isNotNull();
        assertThat(testAccount.getBalance()).isEqualTo(new BigDecimal("1000.00")); // Balance unchanged
        verify(transactionDao, times(1)).save(depositTransaction);
        verifyNoMoreInteractions(transactionDao);
    }

    @Test
    public void getTransactionsByAccountIdReturnsListOfTransactions() {
        // given
        Long accountId = 1L;
        List<Transaction> expectedTransactions = Arrays.asList(
                Transaction.Builder.create()
                        .withTransactionId("tan-1")
                        .withAmount(new BigDecimal("100.00"))
                        .withType(TransactionType.DEPOSIT)
                        .withCurrency(Currency.GBP)
                        .withAccount(testAccount)
                        .build(),
                Transaction.Builder.create()
                        .withTransactionId("tan-2")
                        .withAmount(new BigDecimal("50.00"))
                        .withType(TransactionType.WITHDRAWAL)
                        .withCurrency(Currency.GBP)
                        .withAccount(testAccount)
                        .build()
        );

        given(transactionDao.findByAccountId(eq(accountId))).willReturn(expectedTransactions);

        // when
        List<Transaction> result = sut.getTransactionsByAccountId(accountId);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(expectedTransactions);
        verify(transactionDao, times(1)).findByAccountId(accountId);
        verifyNoMoreInteractions(transactionDao);
    }

    @Test
    public void getTransactionsByAccountIdReturnsEmptyListWhenNoTransactions() {
        // given
        Long accountId = 999L;
        List<Transaction> expectedTransactions = Arrays.asList();

        given(transactionDao.findByAccountId(eq(accountId))).willReturn(expectedTransactions);

        // when
        List<Transaction> result = sut.getTransactionsByAccountId(accountId);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(transactionDao, times(1)).findByAccountId(accountId);
        verifyNoMoreInteractions(transactionDao);
    }
}
