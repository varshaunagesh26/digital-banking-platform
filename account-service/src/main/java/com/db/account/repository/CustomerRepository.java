package com.db.account.repository;

import com.db.account.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {

    Optional<CustomerEntity> findCustomerByCustomerId(Long customerId);

    List<CustomerEntity> findByIsActiveTrue();
}

