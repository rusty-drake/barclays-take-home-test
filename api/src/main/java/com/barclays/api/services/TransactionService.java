package com.barclays.api.services;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.barclays.api.dao.TransactionDao;
import com.barclays.api.domain.Transaction;
import com.barclays.api.domain.enums.TransactionType;

@Service
public class TransactionService {

    private final TransactionDao transactionDao;

    @Autowired
    public TransactionService(
        TransactionDao transactionDao
    ) {
        this.transactionDao = transactionDao;
    }

    public Transaction createTransaction(Transaction transaction) {
        if (transaction.getType() == TransactionType.DEPOSIT) {
            transaction.getAccount().setBalance(
                transaction.getAccount().getBalance().add(transaction.getAmount())
            );
        } else if (transaction.getType() == TransactionType.WITHDRAWAL) {

            if (transaction.getAccount().getBalance().compareTo(transaction.getAmount()) < 0) {
                throw new IllegalArgumentException("Insufficient funds for withdrawal.");
            }
            transaction.getAccount().setBalance(
                transaction.getAccount().getBalance().subtract(transaction.getAmount())
                
            );
        }

        
        return transactionDao.save(transaction);
    }

    public List<Transaction> getTransactionsByAccountId(Long accountId) {
        return transactionDao.findByAccountId(accountId);
    }

}
