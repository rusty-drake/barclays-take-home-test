package com.barclays.api.domain;

import java.math.BigDecimal;
import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.barclays.api.domain.enums.AccountType;
import com.barclays.api.domain.enums.Currency;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @Pattern(regexp = "^01\\d{6}$")
    @Column(unique = true)
    private String accountNumber;

    // @Pattern(regexp = "^10-10-10$")
    @Column
    private String sortCode;

    @NotBlank
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType accountType;

    // @NotNull
    @Digits(integer = 17, fraction = 2)
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    // @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency currency = Currency.GBP;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Calendar createdTimestamp;

    @UpdateTimestamp
    @Column(nullable = false)
    private Calendar updatedTimestamp;

    public Account() {
    }

    public Account(String accountNumber, String sortCode, String name, AccountType accountType, BigDecimal balance, Currency currency, User user) {
        this.accountNumber = accountNumber;
        this.sortCode = sortCode;
        this.name = name;
        this.accountType = accountType;
        this.balance = balance;
        this.currency = currency;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getSortCode() {
        return sortCode;
    }

    public void setSortCode(String sortCode) {
        this.sortCode = sortCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Calendar getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Calendar createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public Calendar getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    public void setUpdatedTimestamp(Calendar updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    // -------- Builder --------

    public static final class Builder {
        private String accountNumber;
        private String sortCode;
        private String name;
        private AccountType accountType;
        private BigDecimal balance;
        private Currency currency;
        private User user;

        public static Builder create() {
            return new Builder();
        }

        public Builder withAccountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
            return this;
        }

        public Builder withSortCode(String sortCode) {
            this.sortCode = sortCode;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withAccountType(AccountType accountType) {
            this.accountType = accountType;
            return this;
        }

        public Builder withBalance(BigDecimal balance) {
            this.balance = balance;
            return this;
        }

        public Builder withCurrency(Currency currency) {
            this.currency = currency;
            return this;
        }

        public Builder withUser(User user) {
            this.user = user;
            return this;
        }

        public Account build() {
            return new Account(accountNumber, sortCode, name, accountType, balance, currency, user);
        }
    }
}
