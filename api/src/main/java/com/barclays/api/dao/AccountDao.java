package com.barclays.api.dao;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.barclays.api.domain.Account;

public interface AccountDao extends CrudRepository<Account, Long> {
    List<Account> findByUserEmail(String email);
    
    @Modifying
    @Transactional
    @Query("UPDATE Account a SET a.balance = :balance WHERE a.id = :accountId")
    void updateBalance(@Param("accountId") Long accountId, @Param("balance") BigDecimal balance);
    

}
