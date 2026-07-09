package com.db.transaction.service;

import com.db.transaction.client.AccountServiceClient;
import com.db.transaction.entity.TransactionEntity;
import com.db.transaction.mapper.CycleAvoidMappingContext;
import com.db.transaction.mapper.TransactionMapper;
import com.db.transaction.repository.TransactionRepository;
import com.digital.backend.events.TransactionEvent;
import com.digital.backend.model.Transaction;
import com.digital.backend.model.TransactionInput;
import com.digital.backend.model.enums.Currency;
import com.digital.backend.model.enums.PaymentType;
import com.digital.backend.model.enums.TransactionStatus;
import com.digital.backend.model.enums.TransactionType;
import com.digital.backend.model.paymentservice.PaymentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.digital.backend.exceptions.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Transactional
@RequiredArgsConstructor
@Slf4j
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    private final TransactionMapper transactionMapper;

    private final AccountServiceClient  accountServiceClient;

    public Transaction getTransactionByTransactionNumber(String transactionNumber){

        log.info("Getting transaction with transaction number {}", transactionNumber);
        Optional<TransactionEntity> transaction = transactionRepository.findTransactionByTransactionNumber(transactionNumber);

        if (transaction.isEmpty()) {
            throw new EntityNotFoundException("Transaction with the given Id not found ");
        }
        return transactionMapper.toDto(transaction.get(), new CycleAvoidMappingContext());
    }

    public List<Transaction> getTransactionByTransactionStatus(TransactionStatus transactionStatus){

        log.info("Getting transactions by transaction status {}", transactionStatus);
        List<TransactionEntity> transactions = transactionRepository.findTransactionByStatus(transactionStatus);

        return transactions.stream()
                .map(transaction ->
                        transactionMapper.toDto(transaction, new CycleAvoidMappingContext()))
                .toList();
    }


    public List<Transaction> getAllTransactions(){

        log.info("Getting all transactions");
        List<TransactionEntity> transactions = transactionRepository.findAll();

        return transactions.stream()
                .map(transaction ->
                        transactionMapper.toDto(transaction, new CycleAvoidMappingContext()))
                .toList();
    }

    public Transaction updateTransactionStatus(String transactionNumber, TransactionStatus newTransactionStatus){

        log.info("Transaction status for transaction {}", transactionNumber);
        Optional<TransactionEntity> transaction = transactionRepository.findTransactionByTransactionNumber(transactionNumber);

        if (transaction.isEmpty()) {
            throw new EntityNotFoundException("Transaction with the given Id not found ");
        }

        transaction.get().setStatus(newTransactionStatus);
        transactionRepository.save(transaction.get());

        return transactionMapper.toDto(transaction.get(), new CycleAvoidMappingContext());
    }

    /***********************************************************************************************/

    public Transaction depositMoney(TransactionInput deposit){

        log.info("Transaction is being created to deposit amount {} to account {}", deposit.getAmount(), deposit.getToAccountNumber());
        TransactionEntity transactionEntity = TransactionEntity.builder()
                        .toAccountNumber(deposit.getToAccountNumber())
                        .amount(deposit.getAmount())
                        .status(TransactionStatus.OPENED)
                        .currency(Currency.INR)
                        .createdBy("transaction-service-deposit")
                        .type(TransactionType.DEPOSIT)
                        .timestamp(Instant.now())
                        .build();
        transactionRepository.save(transactionEntity);
        return transactionMapper.toDto(transactionEntity, new CycleAvoidMappingContext());
    }

    public Transaction withdrawMoney(TransactionInput withdraw){

        log.info("Transaction is being created to withdraw amount {} from account {}", withdraw.getAmount(), withdraw.getFromAccountNumber());
        TransactionEntity transactionEntity = TransactionEntity.builder()
                .fromAccountNumber(withdraw.getFromAccountNumber())
                .amount(withdraw.getAmount())
                .status(TransactionStatus.OPENED)
                .currency(Currency.INR)
                .createdBy("transaction-service-withdraw")
                .type(TransactionType.WITHDRAW)
                .timestamp(Instant.now())
                .build();
        transactionRepository.save(transactionEntity);
        return transactionMapper.toDto(transactionEntity, new CycleAvoidMappingContext());
    }

    public Transaction transferMoney(TransactionInput transfer){

        log.info("Transaction is being created to transfer amount {} from {} to {}", transfer.getAmount(), transfer.getFromAccountNumber(), transfer.getToAccountNumber());
        TransactionEntity transactionEntity = TransactionEntity.builder()
                .fromAccountNumber(transfer.getFromAccountNumber())
                .toAccountNumber(transfer.getToAccountNumber())
                .amount(transfer.getAmount())
                .status(TransactionStatus.OPENED)
                .currency(Currency.INR)
                .createdBy("transaction-service-transfer")
                .type(TransactionType.TRANSFER)
                .timestamp(Instant.now())
                .build();
        transactionRepository.save(transactionEntity);
        return transactionMapper.toDto(transactionEntity, new CycleAvoidMappingContext());
    }

    @Transactional
    public List<TransactionEvent> getTransactionBatch(){

        List<TransactionStatus> statusesToProcess = List.of(
                TransactionStatus.OPENED,
                TransactionStatus.INPROGRESS
        );

        List<TransactionEntity> transactions = transactionRepository.findByStatusIn(statusesToProcess);

        if (transactions.isEmpty()) {
            log.info("No transactions found with OPENED or PENDING status");
            return List.of();
        }

        transactions.forEach(transaction -> {
            if(transaction.getStatus() == TransactionStatus.OPENED){
                transaction.setStatus(TransactionStatus.INPROGRESS);
            }
        });
        transactionRepository.saveAll(transactions);

        return transactions.stream()
                .map(this::mapToEvent)
                .toList();
    }

    private TransactionEvent mapToEvent(TransactionEntity transaction) {
        TransactionEvent event = new TransactionEvent();
        event.setTransactionNumber(transaction.getTransactionNumber() != null
                ? transaction.getTransactionNumber() : null);
        event.setFromAccountNumber(transaction.getFromAccountNumber() != null
                ? transaction.getFromAccountNumber().toString() : null);
        event.setToAccountNumber(transaction.getToAccountNumber() != null
                ? transaction.getToAccountNumber().toString() : null);
        event.setAmount(transaction.getAmount() != null
                ? transaction.getAmount().toString() : null);
        event.setCurrency(transaction.getCurrency() != null
                ? transaction.getCurrency().name() : null);
        event.setType(transaction.getType() != null
                ? transaction.getType().name() : null);
        event.setStatus(transaction.getStatus() != null
                ? transaction.getStatus().name() : null);
        event.setReferenceNumber(transaction.getReferenceNumber());
        event.setTimestamp(transaction.getTimestamp() != null
                ? transaction.getTimestamp().toString() : null);
        return event;
    }

    @Transactional
    public void updateTransactionResponseStatus(TransactionEvent transactionEvent){
        log.info("Transaction status of transaction {} is {}",  transactionEvent.getTransactionNumber(), transactionEvent.getStatus());

        Optional<TransactionEntity> optionalTransactionEntity = transactionRepository.findTransactionByTransactionNumber(transactionEvent.getTransactionNumber());

        if(optionalTransactionEntity.isEmpty()){
            log.info("No transaction found with transaction number {}", transactionEvent.getTransactionNumber());
            return;
        }

        TransactionEntity transactionEntity = optionalTransactionEntity.get();
        TransactionStatus eventStatus = TransactionStatus.valueOf(transactionEvent.getStatus());

        if(eventStatus == TransactionStatus.COMPLETED) {
            transactionEntity.setStatus(TransactionStatus.COMPLETED);
            transactionRepository.save(transactionEntity);
            log.info("Transaction {} status updated is completed", transactionEvent.getTransactionNumber());
        }
        else if(eventStatus == TransactionStatus.FAILED){
            transactionEntity.setStatus(TransactionStatus.FAILED);
            transactionRepository.save(transactionEntity);
            log.info("Transaction {} status updated is failed", transactionEvent.getTransactionNumber());
        }
    }

    public void processPaymentEvent(PaymentEvent event) {
        if (transactionRepository.existsByReferenceNumber(event.getPaymentId())) {
            log.warn("Duplicate payment event received for paymentId: {}", event.getPaymentId());
            return;
        }

        TransactionEntity transaction = TransactionEntity.builder()
                .fromAccountNumber(event.getFromAccountNumber() != null
                        ? Long.parseLong(event.getFromAccountNumber()) : null)
                .toAccountNumber(event.getToAccountNumber() != null
                        ? Long.parseLong(event.getToAccountNumber()) : null)
                .amount(event.getAmount())
                .currency(Currency.INR)
                .type(mapToTransactionType(event.getPaymentType()))
                .status(TransactionStatus.OPENED)
                .createdBy("payment-service")
                .timestamp(Instant.now())
                .referenceNumber(event.getPaymentId())
                .build();

        transactionRepository.save(transaction);
        log.info("Transaction created for paymentId: {}", event.getPaymentId());
    }

    private TransactionType mapToTransactionType(PaymentType paymentType) {
        if (paymentType == null)
            return TransactionType.TRANSFER;
        return switch (paymentType) {
            case DEBIT    -> TransactionType.WITHDRAW;
            case CREDIT   -> TransactionType.DEPOSIT;
            case TRANSFER -> TransactionType.TRANSFER;
        };
    }
}
