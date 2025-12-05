package com.barclays.api.dao;

import org.springframework.data.repository.CrudRepository;

import com.barclays.api.domain.Transaction;

public interface  TransactionDao extends CrudRepository<Transaction, Long> {

}
