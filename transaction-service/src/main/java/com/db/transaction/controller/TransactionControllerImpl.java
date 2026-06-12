package com.db.transaction.controller;

import com.db.transaction.service.TransactionService;
import com.digital.backend.model.Transaction;
import com.digital.backend.model.TransactionInput;
import com.digital.backend.model.enums.TransactionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class TransactionControllerImpl implements TransactionControllerSpec {

    private final TransactionService transactionService;


    @Override
    public ResponseEntity<Transaction> getTransactionByTransactionNumber(String transactionNumber){
        return new ResponseEntity<>(
                transactionService.getTransactionByTransactionNumber(transactionNumber), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<Transaction>> getTransactionsByTransactionStatus(TransactionStatus transactionStatus){
        return new ResponseEntity<>(
                transactionService.getTransactionByTransactionStatus(transactionStatus), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<Transaction>> getAllTransactions(){
        return new ResponseEntity<>(
                transactionService.getAllTransactions(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Transaction> updateTransactionStatus(String transactionNumber, TransactionStatus transactionStatus){
        return new ResponseEntity<>(
                transactionService.updateTransactionStatus(transactionNumber, transactionStatus), HttpStatus.OK);
    }

    /*******************************************************************************************/

    @Override
    public ResponseEntity<Transaction> depositMoney(TransactionInput deposit){
        if(deposit.getToAccountNumber() == null){
            return ResponseEntity.badRequest()
                    .header("to account error", "toAccountNumber is required for deposit")
                    .build();
        }

        return new ResponseEntity<>(
                transactionService.depositMoney(deposit), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Transaction> withdrawMoney(TransactionInput withdraw){
        if(withdraw.getFromAccountNumber() == null){
            return ResponseEntity.badRequest()
                    .header("from account error", "fromAccountNumber is required for withdraw")
                    .build();
        }

        return new ResponseEntity<>(
                transactionService.withdrawMoney(withdraw), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Transaction> transferMoney(TransactionInput transfer){
        if(transfer.getFromAccountNumber() == null || transfer.getToAccountNumber() == null){
            return ResponseEntity.badRequest()
                    .header("account information error", "fromAccountNumber and toAccountNumber is required for transfer")
                    .build();
        }

        return new ResponseEntity<>(
                transactionService.transferMoney(transfer), HttpStatus.OK);
    }
}
