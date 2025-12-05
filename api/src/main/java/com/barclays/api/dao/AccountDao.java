package com.barclays.api.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.barclays.api.domain.Account;

public interface AccountDao extends CrudRepository<Account, Long> {
    List<Account> findByUserEmail(String email);

}
