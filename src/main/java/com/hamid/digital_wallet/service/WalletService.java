package com.hamid.digital_wallet.service;

import com.hamid.digital_wallet.entity.Transaction;
import com.hamid.digital_wallet.entity.Transfer;
import com.hamid.digital_wallet.entity.Wallet;
import com.hamid.digital_wallet.repository.TransactionRepository;
import com.hamid.digital_wallet.repository.TransferRepository;
import com.hamid.digital_wallet.repository.UserRepository;
import com.hamid.digital_wallet.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class WalletService {

    private final TransactionRepository transactionRepository;
    private final TransferRepository transferRepository;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    public WalletService(TransactionRepository transactionRepository, TransferRepository transferRepository, UserRepository userRepository, WalletRepository walletRepository) {
        this.transactionRepository = transactionRepository;
        this.transferRepository = transferRepository;
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
    }

//   top up means , external bank account to wallet.
//   this is not a wallet to wallet transaction.
    @Transactional
    public Transaction topUp(String walletId, BigDecimal amount, String referenceId){

//      CHecking if the account balance is sufficient.
//        if(amount.compareTo(BigDecimal.ZERO)<=0){
//            throw new IllegalArgumentException("Amount cannot be less than 0.");
//        }

//      Checking for existing transaction of the reference Id
        Optional<Transaction> existing = transactionRepository.findByReferenceId(referenceId);
        if(existing.isPresent()){
            return existing.get();
        }
        Wallet wallet = walletRepository.findById(walletId).orElseThrow(()-> new IllegalArgumentException("Wallet not found"));

//      Logging the transaction record.
        Transaction txn = Transaction.builder()
                .wallet(wallet)
                .type(Transaction.TransactionType.CREDIT)
                .amount(amount)
                .status(Transaction.TransactionStatus.INITIATED)
                .referenceId(referenceId)
                .build();

        transactionRepository.save(txn);

        try {
            wallet.setBalance(wallet.getBalance().add(amount));
            walletRepository.save(wallet);
            txn.setStatus(Transaction.TransactionStatus.SUCCESS);
            return transactionRepository.save(txn);
        } catch(Exception e){
            txn.setStatus(Transaction.TransactionStatus.FAILED);
            transactionRepository.save(txn);
            throw e;
        }
    }
//    ---------------------- Transaction top-up ends ------------------------
//    ---------------------- Transaction PAY/DEBIT begins ------------------------
    @Transactional
    public Transaction debit(String walletId, BigDecimal amount ,String referenceId){
//        if(amount.compareTo(BigDecimal.ZERO)<=0){
//            throw new IllegalArgumentException("Amount less than 0");
//        }
//
//        Optional<Transaction> existing = transactionRepository.findByReferenceId(referenceId);
//        if(existing.isPresent()){
//            return existing.get();
//        }

        Wallet wallet = walletRepository.findById(walletId).orElseThrow(()-> new IllegalArgumentException("Wallet not found."));

        if(wallet.getBalance().compareTo(amount)<0){
            throw new IllegalArgumentException("Insufficient Balance");
        }

        Transaction txn = Transaction.builder()
                .wallet(wallet)
                .type(Transaction.TransactionType.DEBIT)
                .amount(amount)
                .status(Transaction.TransactionStatus.INITIATED)
                .referenceId(referenceId)
                .build();

        transactionRepository.save(txn);

        try{

            wallet.setBalance(wallet.getBalance().subtract(amount));
            walletRepository.save(wallet);

            txn.setStatus(Transaction.TransactionStatus.SUCCESS);
            return transactionRepository.save(txn);

        } catch(Exception e){

            txn.setStatus(Transaction.TransactionStatus.FAILED);
            transactionRepository.save(txn);
            throw e;

        }
    }
//    ---------------------- Transaction DEBIT ends ------------------------
    @Transactional
    public void transfer(String fromWalletId, String toWalletId, BigDecimal amount, String referenceId) {

        if(amount.compareTo(BigDecimal.ZERO)<=0){
            throw new IllegalArgumentException("Amount has to be greater than zero.");
        }

        if(transactionRepository.existsByReferenceId(referenceId)){
            return; // means already processed
        }



//      DEBIT transaction from wallet.
        Transaction debitTxn = debit(fromWalletId, amount, referenceId+"_DEBIT");

//      CREDIT transaction to wallet.
        Transaction creditTxn = topUp(toWalletId, amount, referenceId+"_CREDIT");

//      Record transfer
        Transfer transfer = Transfer.builder()
                .fromWallet(debitTxn.getWallet())
                .toWallet(creditTxn.getWallet())
                .amount(amount)
                .build();

        transferRepository.save(transfer);
    }

}
