package com.hamid.digital_wallet.service;

import com.hamid.digital_wallet.entity.Transaction;
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

    //  top up means , external bank account to wallet.
//    this is not a wallet to wallet transaction.
    @Transactional
    public Transaction topUp(String walletId, BigDecimal amount, String referenceId){

//        CHecking if the account balance is sufficient.
        if(amount.compareTo(BigDecimal.ZERO)<=0){
            throw new IllegalArgumentException("Amount cannot be less than 0.");
        }

//        Checking for existing transaction of the reference Id
        Optional<Transaction> existing = transactionRepository.findByReferenceId(referenceId);
        if(existing.isPresent()){
            return existing.get();
        }

//        Updating balance
        Optional<Wallet> walletOpt = walletRepository.findById(walletId);
        if (!walletOpt.isPresent()) {
            throw new IllegalArgumentException("Wallet not found");
        }
        Wallet wallet = walletOpt.get();
        walletRepository.save(wallet);

//        Logging the transaction record.
        Transaction txn = Transaction.builder()
                .wallet(wallet)
                .type(Transaction.TransactionType.CREDIT)
                .amount(amount)
                .status(Transaction.TransactionStatus.SUCCESS)
                .referenceId(referenceId)
                .build();

        return transactionRepository.save(txn);

    }

}
