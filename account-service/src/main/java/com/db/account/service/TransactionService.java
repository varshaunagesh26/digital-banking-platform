package com.db.account.service;

import com.db.account.entity.AccountEntity;
import com.db.account.entity.EventHistory;
import com.db.account.repository.BranchRepository;
import com.db.account.response.publisher.TransactionEventResponsePublisher;
import com.db.account.repository.AccountRepository;
import com.db.account.repository.EventHistoryRepository;
import com.digital.backend.events.TransactionEvent;
import com.digital.backend.model.enums.EventStatus;
import com.digital.backend.model.enums.TransactionStatus;
import com.digital.backend.model.enums.TransactionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Slf4j
@Service
public class TransactionService {

    private final AccountRepository accountRepository;

    private final TransactionEventResponsePublisher transactionPublisher;

    private final EventHistoryRepository eventHistoryRepository;

    private final BranchRepository branchRepository;


    public void processDeposit(TransactionEvent transactionEvent) {

        String transactionNumber = transactionEvent.getTransactionNumber();
        Optional<EventHistory> optionalEventHistory = eventHistoryRepository.findEventHistoryByTransactionNumber(transactionNumber);

        if (optionalEventHistory.isPresent()) {
            EventHistory eventHistory = optionalEventHistory.get();

            if (eventHistory.getStatus() == TransactionStatus.COMPLETED) {
                log.info("Amount was already credited");
                publishResponseEvent(transactionEvent);
                return;
            }
        }
        performDeposit(transactionEvent);
        publishResponseEvent(transactionEvent);

    }

    public void processWithdrawal(TransactionEvent transactionEvent) {

        String transactionNumber = transactionEvent.getTransactionNumber();
        Optional<EventHistory> optionalEventHistory = eventHistoryRepository.findEventHistoryByTransactionNumber(transactionNumber);

        if (optionalEventHistory.isPresent()) {

            EventHistory eventHistory = optionalEventHistory.get();

            if (eventHistory.getStatus() == TransactionStatus.COMPLETED) {
                log.info("Amount was already debited");
                publishResponseEvent(transactionEvent);
                return;
            }
        }
        performWithdrawal(transactionEvent);
        publishResponseEvent(transactionEvent);
    }

    public void processTransfer(TransactionEvent transactionEvent) {

        String transactionNumber = transactionEvent.getTransactionNumber();

        Optional<EventHistory> optionalEventHistory = eventHistoryRepository.findEventHistoryByTransactionNumber(transactionNumber);

        if (optionalEventHistory.isPresent()) {

            EventHistory eventHistory = optionalEventHistory.get();

            if (eventHistory.getStatus() == TransactionStatus.COMPLETED) {
                log.info("Transfer was already performed");
                publishResponseEvent(transactionEvent);
                return;
            }
        }
        performTransfer(transactionEvent);
        publishResponseEvent(transactionEvent);

    }

    private void publishResponseEvent(TransactionEvent transactionEvent) {
        log.info("Publishing response for transaction: {}, status: {}",
                transactionEvent.getTransactionNumber(), transactionEvent.getStatus());
        transactionPublisher.publishResponseEvent(transactionEvent);
    }

    private void performDeposit(TransactionEvent transactionEvent) {

        log.info("Performing deposit of account {}", transactionEvent.getFromAccountNumber());

        if (!isBranchValid(transactionEvent.getToIFSCCode())) {
            log.error("Branch with IFSC {} not found for DEPOSIT, transaction {}",
                    transactionEvent.getToIFSCCode(), transactionEvent.getTransactionNumber());
            transactionEvent.setStatus(String.valueOf(TransactionStatus.FAILED));
            updateEventHistory(transactionEvent, TransactionStatus.FAILED);
            return;
        }

        Long accountNumber = Long.parseLong(transactionEvent.getToAccountNumber());

        AccountEntity account = accountRepository.findAccountByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found: " + accountNumber));

        if (Boolean.FALSE.equals(account.getIsActive())) {
            log.info("Account {} has been deactivated", accountNumber);
            transactionEvent.setStatus(String.valueOf(TransactionStatus.FAILED));
            updateEventHistory(transactionEvent, TransactionStatus.FAILED);
            return;
        }

        double amount = Double.parseDouble(transactionEvent.getAmount());
        double newBalance = account.getAccountBalance() + amount;
        account.setAccountBalance(newBalance);
        accountRepository.save(account);

        updateEventHistory(transactionEvent, TransactionStatus.COMPLETED);

        log.info("Amount {} has been credited to the account {}", newBalance, accountNumber);
        transactionEvent.setStatus(String.valueOf(TransactionStatus.COMPLETED));

    }


    private void performWithdrawal(TransactionEvent transactionEvent) {

        log.info("Performing withdrawal of account {}", transactionEvent.getFromAccountNumber());

        if (!isBranchValid(transactionEvent.getFromIFSCCode())) {
            log.error("Branch with IFSC {} not found for WITHDRAWAL, transaction {}",
                    transactionEvent.getFromIFSCCode(), transactionEvent.getTransactionNumber());
            transactionEvent.setStatus(String.valueOf(TransactionStatus.FAILED));
            updateEventHistory(transactionEvent, TransactionStatus.FAILED);
            return;
        }

        Long accountNumber = Long.parseLong(transactionEvent.getFromAccountNumber());

        AccountEntity account = accountRepository.findAccountByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found: " + accountNumber));

        if (Boolean.FALSE.equals(account.getIsActive())) {
            log.error("Account {} has been deactivated", accountNumber);
            transactionEvent.setStatus(String.valueOf(TransactionStatus.FAILED));
            updateEventHistory(transactionEvent, TransactionStatus.FAILED);
            return;
        }


        double amount = Double.parseDouble(transactionEvent.getAmount());

        if (account.getAccountBalance() < amount) {
            log.info("Balance insufficient. Withdrawal can't be performed");
            transactionEvent.setStatus(String.valueOf(TransactionStatus.FAILED));
            updateEventHistory(transactionEvent, TransactionStatus.FAILED);

        } else {
            account.setAccountBalance(account.getAccountBalance() - amount);
            accountRepository.save(account);

            updateEventHistory(transactionEvent, TransactionStatus.COMPLETED);

            transactionEvent.setStatus(String.valueOf(TransactionStatus.COMPLETED));

            log.info("Amount {} has been debited from the account {}", amount, accountNumber);
        }
    }

    private void performTransfer(TransactionEvent transactionEvent) {

        log.info("Performing transfer from  account {} to account {}", transactionEvent.getFromAccountNumber(), transactionEvent.getToAccountNumber());

        if (!isBranchValid(transactionEvent.getFromIFSCCode())) {
            log.error("Sender branch with IFSC {} not found for TRANSFER, transaction {}",
                    transactionEvent.getFromIFSCCode(), transactionEvent.getTransactionNumber());
            transactionEvent.setStatus(String.valueOf(TransactionStatus.FAILED));
            updateEventHistory(transactionEvent, TransactionStatus.FAILED);
            return;
        }

        if (!isBranchValid(transactionEvent.getToIFSCCode())) {
            log.error("Receiver branch with IFSC {} not found for TRANSFER, transaction {}",
                    transactionEvent.getToIFSCCode(), transactionEvent.getTransactionNumber());
            transactionEvent.setStatus(String.valueOf(TransactionStatus.FAILED));
            updateEventHistory(transactionEvent, TransactionStatus.FAILED);
            return;
        }

        Long senderAccountNumber = Long.parseLong(transactionEvent.getFromAccountNumber());
        Long receiverAccountNumber = Long.parseLong(transactionEvent.getToAccountNumber());

        AccountEntity senderAccount = accountRepository.findAccountByAccountNumber(senderAccountNumber)
                .orElseThrow(() -> new RuntimeException("Sender account not found: " + senderAccountNumber));


        AccountEntity receiverAccount = accountRepository.findAccountByAccountNumber(receiverAccountNumber)
                .orElseThrow(() -> new RuntimeException("Receiver account not found: " + receiverAccountNumber));


        if (Boolean.FALSE.equals(senderAccount.getIsActive()) || Boolean.FALSE.equals(receiverAccount.getIsActive())) {
            log.error("The Sender account {} activation status is: {}", senderAccountNumber, senderAccount.getIsActive());
            log.error("The Receiver account {} activation status is: {}", receiverAccountNumber, receiverAccount.getIsActive());

            transactionEvent.setStatus(String.valueOf(TransactionStatus.FAILED));
            updateEventHistory(transactionEvent, TransactionStatus.FAILED);
            return;
        }

        double amount = Double.parseDouble(transactionEvent.getAmount());

        if (senderAccount.getAccountBalance() < amount) {
            log.info("Balance insufficient for transfer to be performed");
            transactionEvent.setStatus(String.valueOf(TransactionStatus.FAILED));
            updateEventHistory(transactionEvent, TransactionStatus.FAILED);
            return;
        }

        senderAccount.setAccountBalance(senderAccount.getAccountBalance() - amount);
        receiverAccount.setAccountBalance(receiverAccount.getAccountBalance() + amount);
        accountRepository.save(senderAccount);
        accountRepository.save(receiverAccount);

        updateEventHistory(transactionEvent, TransactionStatus.COMPLETED);
        transactionEvent.setStatus(String.valueOf(TransactionStatus.COMPLETED));

        log.info("Amount {} has been sent from {} to {}", amount, senderAccountNumber, receiverAccountNumber);
    }


    public void setEventHistory(TransactionEvent transactionEvent, TransactionType transactionType, TransactionStatus transactionStatus) throws SQLIntegrityConstraintViolationException {
        EventHistory eventHistory = new EventHistory();
        eventHistory.setTransactionNumber(transactionEvent.getTransactionNumber());
        eventHistory.setType(transactionType);
        eventHistory.setEventStatus(EventStatus.RESPONSE);
        eventHistory.setStatus(transactionStatus);
        eventHistoryRepository.save(eventHistory);
    }

    private void updateEventHistory(TransactionEvent transactionEvent, TransactionStatus transactionStatus) {
        String transactionNumber = transactionEvent.getTransactionNumber();
        Optional<EventHistory> eventHistoryByTransactionNumber = eventHistoryRepository.findEventHistoryByTransactionNumber(transactionNumber);
        if (eventHistoryByTransactionNumber.isPresent()) {
            EventHistory eventHistory = eventHistoryByTransactionNumber.get();
            eventHistory.setStatus(transactionStatus);
            eventHistoryRepository.save(eventHistory);
        }

    }

    private boolean isBranchValid(String ifscCode){

        if (ifscCode == null || ifscCode.isBlank()) {
            log.info("IFSC code not provided, skipping branch validation");
            return true;
        }

        boolean branchExists = branchRepository.findByBranchIFSC(ifscCode).isPresent();
        if (!branchExists) {
            log.warn("Branch with IFSC {} not found", ifscCode);
        }
        return branchExists;
    }

    public boolean isAlreadyProcessed(String transactionNumber) {
        return eventHistoryRepository
                .findEventHistoryByTransactionNumber(transactionNumber)
                .map(e -> e.getStatus() == TransactionStatus.COMPLETED
                        || e.getStatus() == TransactionStatus.FAILED)
                .orElse(false);
    }

    public void republishExistingResult(String transactionNumber) {
        log.info("Republishing existing result for transaction {}", transactionNumber);
        eventHistoryRepository.findEventHistoryByTransactionNumber(transactionNumber)
                .ifPresentOrElse(
                        eventHistory -> {
                            TransactionEvent responseEvent = new TransactionEvent();
                            responseEvent.setTransactionNumber(transactionNumber);
                            responseEvent.setStatus(String.valueOf(eventHistory.getStatus()));
                            responseEvent.setType(String.valueOf(eventHistory.getType()));
                            log.info("Republishing existing result for transaction {} with status {}",
                                    transactionNumber, eventHistory.getStatus());
                            publishResponseEvent(responseEvent);
                        },
                        () -> log.warn("No event history found for transaction {}, cannot republish",
                                transactionNumber)
                );
    }

}
