package com.barclays.api.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.CreationTimestamp;

import com.barclays.api.domain.enums.Currency;
import com.barclays.api.domain.enums.TransactionType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    // @Pattern(regexp = "^tan-[A-Za-z0-9]+$", message = "Transaction ID must match pattern tan-[A-Za-z0-9]+")
    @Column(unique = true, nullable = false)
    private String transactionId;

    @NotNull
    @DecimalMin(value = "0.00", message = "Amount must be greater than or equal to 0.00")
    @DecimalMax(value = "10000.00", message = "Amount must be less than or equal to 10000.00")
    @Digits(integer = 5, fraction = 2)
    @Column(nullable = false, precision = 7, scale = 2)
    private BigDecimal amount;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency currency = Currency.GBP;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    @JsonIgnore
    private Account account;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdTimestamp;

    public Transaction() {
    }

    public Transaction(String transactionId, BigDecimal amount, Currency currency, TransactionType type, 
                      User user, Account account) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.currency = currency;
        this.type = type;
        this.user = user;
        this.account = account;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public LocalDateTime getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(LocalDateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
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
        private String transactionId;
        private BigDecimal amount;
        private Currency currency;
        private TransactionType type;
        private User user;
        private Account account;

        public static Builder create() {
            return new Builder();
        }

        public Builder withTransactionId(String transactionId) {
            this.transactionId = transactionId;
            return this;
        }

        public Builder withAmount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder withCurrency(Currency currency) {
            this.currency = currency;
            return this;
        }

        public Builder withType(TransactionType type) {
            this.type = type;
            return this;
        }

        public Builder withUser(User user) {
            this.user = user;
            return this;
        }

        public Builder withAccount(Account account) {
            this.account = account;
            return this;
        }

        public Transaction build() {
            return new Transaction(transactionId, amount, currency, type, user, account);
        }
    }
}
