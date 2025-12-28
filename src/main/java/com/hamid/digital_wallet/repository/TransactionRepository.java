package com.hamid.digital_wallet.repository;

import com.hamid.digital_wallet.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
    Optional<Transaction> findByReferenceId(String referenceId);

//    List of transactions
//    List<Transaction> findByWalletOrderByCreatedAtDesc(Wallet wallet);
}
