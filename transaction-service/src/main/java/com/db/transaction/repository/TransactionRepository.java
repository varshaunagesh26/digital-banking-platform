package com.db.transaction.repository;

import com.db.transaction.entity.TransactionEntity;
import com.digital.backend.model.enums.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity,Long> {

    Optional<TransactionEntity> findTransactionByTransactionNumber(String transactionNumber);

    List<TransactionEntity> findTransactionByStatus(TransactionStatus status);

    List<TransactionEntity> findByStatusIn(List<TransactionStatus> status);
}
