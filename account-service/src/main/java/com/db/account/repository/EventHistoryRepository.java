package com.db.account.repository;

import com.db.account.entity.EventHistory;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Repository
public interface EventHistoryRepository extends JpaRepository<EventHistory, Long>{

    Optional<EventHistory> findEventHistoryByTransactionNumber(String transactionNumber);
}
