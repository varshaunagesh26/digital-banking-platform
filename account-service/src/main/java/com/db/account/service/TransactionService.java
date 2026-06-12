package com.db.account.service;

import com.db.account.entity.AccountEntity;
import com.db.account.entity.EventHistory;
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

import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Slf4j
@Service
public class TransactionService {

    private final AccountRepository accountRepository;

    private final TransactionEventResponsePublisher transactionPublisher;

    private final EventHistoryRepository eventHistoryRepository;


    public void processDeposit(TransactionEvent transactionEvent) {

        String transactionNumber = transactionEvent.getTransactionNumber();
        Optional<EventHistory> optionalEventHistory = eventHistoryRepository.findEventHistoryByTransactionNumber(transactionNumber);

        if (optionalEventHistory.isPresent()) {
            EventHistory eventHistory = optionalEventHistory.get();

            if (eventHistory.getStatus() == TransactionStatus.COMPLETED) {
                log.info("Amount was already credited");
                log.info("Publishing response for transaction: {}, status: {}",
                        transactionEvent.getTransactionNumber(), transactionEvent.getStatus());
                transactionPublisher.publishResponseEvent(transactionEvent);
                return;
            }
        }
        performDeposit(transactionEvent);
        log.info("Publishing response for transaction: {}, status: {}",
                transactionEvent.getTransactionNumber(), transactionEvent.getStatus());
        transactionPublisher.publishResponseEvent(transactionEvent);

    }

    public void processWithdrawal(TransactionEvent transactionEvent) {

        String transactionNumber = transactionEvent.getTransactionNumber();
        Optional<EventHistory> optionalEventHistory = eventHistoryRepository.findEventHistoryByTransactionNumber(transactionNumber);

        if (optionalEventHistory.isPresent()) {

            EventHistory eventHistory = optionalEventHistory.get();

            if (eventHistory.getStatus() == TransactionStatus.COMPLETED) {
                log.info("Amount was already debited");
                log.info("Publishing response for transaction: {}, status: {}",
                        transactionEvent.getTransactionNumber(), transactionEvent.getStatus());
                transactionPublisher.publishResponseEvent(transactionEvent);
                return;
            }
        }
        performWithdrawal(transactionEvent);
        log.info("Publishing response for transaction: {}, status: {}",
                transactionEvent.getTransactionNumber(), transactionEvent.getStatus());
        transactionPublisher.publishResponseEvent(transactionEvent);
    }

    public void processTransfer(TransactionEvent transactionEvent) {

        String transactionNumber = transactionEvent.getTransactionNumber();

        Optional<EventHistory> optionalEventHistory = eventHistoryRepository.findEventHistoryByTransactionNumber(transactionNumber);

        if (optionalEventHistory.isPresent()) {

            EventHistory eventHistory = optionalEventHistory.get();

            if (eventHistory.getStatus() == TransactionStatus.COMPLETED) {
                log.info("Transfer was already performed");
                transactionPublisher.publishResponseEvent(transactionEvent);
                return;
            }
        }
        performTransfer(transactionEvent);
        transactionPublisher.publishResponseEvent(transactionEvent);

    }

    private void performDeposit(TransactionEvent transactionEvent) {

        log.info("Performing deposit of account {}", transactionEvent.getFromAccountNumber());
        Long accountNumber = Long.parseLong(transactionEvent.getToAccountNumber());

        AccountEntity account = accountRepository.findAccountByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found: " + accountNumber));

        if(Boolean.FALSE.equals(account.getIsActive())){
            log.info("Account {} has been deactivated", accountNumber);

            transactionEvent.setStatus(String.valueOf(TransactionStatus.FAILED));

            EventHistory eventHistory = new EventHistory();

            eventHistory.setTransactionNumber(transactionEvent.getTransactionNumber());
            eventHistory.setType(TransactionType.DEPOSIT);
            eventHistory.setEventStatus(EventStatus.RESPONSE);
            eventHistory.setStatus(TransactionStatus.FAILED);
            eventHistoryRepository.save(eventHistory);
            return;
        }

        double amount = Double.parseDouble(transactionEvent.getAmount());
        double newBalance = account.getAccountBalance() + amount;
        account.setAccountBalance(newBalance);
        accountRepository.save(account);

        EventHistory eventHistory = new EventHistory();

        eventHistory.setTransactionNumber(transactionEvent.getTransactionNumber());
        eventHistory.setType(TransactionType.DEPOSIT);
        eventHistory.setEventStatus(EventStatus.RESPONSE);
        eventHistory.setStatus(TransactionStatus.COMPLETED);

        transactionEvent.setStatus(String.valueOf(TransactionStatus.COMPLETED));


        eventHistoryRepository.save(eventHistory);

        log.info("Amount {} has been credited to the account {}", newBalance, accountNumber);
        return;
    }

    private void performWithdrawal(TransactionEvent transactionEvent) {

        log.info("Performing withdrawal of account {}", transactionEvent.getFromAccountNumber());
        Long accountNumber = Long.parseLong(transactionEvent.getFromAccountNumber());

        AccountEntity account = accountRepository.findAccountByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found: " + accountNumber));

        if(Boolean.FALSE.equals(account.getIsActive())){
            log.error("Account {} has been deactivated", accountNumber);

            transactionEvent.setStatus(String.valueOf(TransactionStatus.FAILED));

            EventHistory eventHistory = new EventHistory();

            eventHistory.setTransactionNumber(transactionEvent.getTransactionNumber());
            eventHistory.setType(TransactionType.WITHDRAW);
            eventHistory.setEventStatus(EventStatus.RESPONSE);
            eventHistory.setStatus(TransactionStatus.FAILED);
            eventHistoryRepository.save(eventHistory);
            return;
        }

        double amount = Double.parseDouble(transactionEvent.getAmount());

        if (account.getAccountBalance() < amount) {
            log.info("Balance insufficient. Withdrawal can't be performed");
            transactionEvent.setStatus(String.valueOf(TransactionStatus.FAILED));
            EventHistory eventHistory = new EventHistory();
            eventHistory.setTransactionNumber(transactionEvent.getTransactionNumber());
            eventHistory.setType(TransactionType.WITHDRAW);
            eventHistory.setEventStatus(EventStatus.RESPONSE);
            eventHistory.setStatus(TransactionStatus.FAILED);
            eventHistoryRepository.save(eventHistory);
            return;
        }
        account.setAccountBalance(account.getAccountBalance() - amount);
        accountRepository.save(account);

        EventHistory eventHistory = new EventHistory();

        eventHistory.setTransactionNumber(transactionEvent.getTransactionNumber());
        eventHistory.setType(TransactionType.WITHDRAW);
        eventHistory.setEventStatus(EventStatus.RESPONSE);
        eventHistory.setStatus(TransactionStatus.COMPLETED);

        transactionEvent.setStatus(String.valueOf(TransactionStatus.COMPLETED));

        eventHistoryRepository.save(eventHistory);

        log.info("Amount {} has been debited from the account {}", amount, accountNumber);
    }

    private void performTransfer(TransactionEvent transactionEvent) {

        log.info("Performing transfer from  account {} to account {}", transactionEvent.getFromAccountNumber(), transactionEvent.getToAccountNumber());
        Long senderAccountNumber = Long.parseLong(transactionEvent.getFromAccountNumber());
        Long receiverAccountNumber = Long.parseLong(transactionEvent.getToAccountNumber());

        AccountEntity senderAccount = accountRepository.findAccountByAccountNumber(senderAccountNumber)
                .orElseThrow(() -> new RuntimeException("Sender account not found: " + senderAccountNumber));

        if(Boolean.FALSE.equals(senderAccount.getIsActive())){
            log.error("Sender account {} has been deactivated", senderAccountNumber);
            transactionEvent.setStatus(String.valueOf(TransactionStatus.CANCELLED));

            EventHistory eventHistory = new EventHistory();

            eventHistory.setTransactionNumber(transactionEvent.getTransactionNumber());
            eventHistory.setType(TransactionType.TRANSFER);
            eventHistory.setEventStatus(EventStatus.RESPONSE);
            eventHistory.setStatus(TransactionStatus.FAILED);
            eventHistoryRepository.save(eventHistory);

            return;
        }

        AccountEntity receiverAccount = accountRepository.findAccountByAccountNumber(receiverAccountNumber)
                .orElseThrow(() -> new RuntimeException("Receiver account not found: " + receiverAccountNumber));

        if(Boolean.FALSE.equals(receiverAccount.getIsActive())){
            log.error("Sender account {} has been deactivated", senderAccountNumber);
            transactionEvent.setStatus(String.valueOf(TransactionStatus.CANCELLED));

            EventHistory eventHistory = new EventHistory();

            eventHistory.setTransactionNumber(transactionEvent.getTransactionNumber());
            eventHistory.setType(TransactionType.TRANSFER);
            eventHistory.setEventStatus(EventStatus.RESPONSE);
            eventHistory.setStatus(TransactionStatus.FAILED);
            eventHistoryRepository.save(eventHistory);

            return;
        }

        double amount = Double.parseDouble(transactionEvent.getAmount());

        if (senderAccount.getAccountBalance() < amount) {
            log.info("Balance insufficient for transfer to be performed");
        }

        senderAccount.setAccountBalance(senderAccount.getAccountBalance() - amount);
        receiverAccount.setAccountBalance(receiverAccount.getAccountBalance() + amount);
        accountRepository.save(senderAccount);
        accountRepository.save(receiverAccount);

        EventHistory eventHistory = new EventHistory();

        eventHistory.setTransactionNumber(transactionEvent.getTransactionNumber());
        eventHistory.setType(TransactionType.TRANSFER);
        eventHistory.setEventStatus(EventStatus.RESPONSE);
        eventHistory.setStatus(TransactionStatus.COMPLETED);

        transactionEvent.setStatus(String.valueOf(TransactionStatus.COMPLETED));

        eventHistoryRepository.save(eventHistory);

        log.info("Amount {} has been sent from {} to {}", amount, senderAccountNumber, receiverAccountNumber);
    }

}
