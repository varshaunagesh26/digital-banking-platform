package com.db.account.repository;

import com.db.account.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

    List<AccountEntity> findAllByIsActiveTrue();

    Optional<AccountEntity> findAccountByAccountNumber(Long accountNumber);
}

