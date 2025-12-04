package com.barclays.api.dao;
import org.springframework.data.repository.CrudRepository;

import com.barclays.api.domain.User;

public interface UserCrudRepository extends CrudRepository<User, Long> {
    User findByEmail(String email);
}
