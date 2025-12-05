package com.barclays.api.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.barclays.api.domain.Transaction;

public interface  TransactionDao extends CrudRepository<Transaction, Long> {
    List<Transaction> findByAccountId(Long accountId);

}
