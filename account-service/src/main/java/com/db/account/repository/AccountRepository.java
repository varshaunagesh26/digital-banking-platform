package com.db.account.repository;

import com.db.account.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

    boolean isActiveTrue(long accountNumber);
    boolean isActiveFalse(long accountNumber);
    List<AccountEntity> findAllByIsActiveFalse();
    List<AccountEntity> findAllByIsActiveTrue();

}

