package com.hamid.digital_wallet.repository;

import com.hamid.digital_wallet.entity.Wallet;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("Select w FROM Wallet w WHERE w.id =: userId")
    Optional<Wallet> findByUserId(String userId);

//  Prevents race conditions, two concurrent request now cannot write at the same time, leading to consistency or transactions in wallet.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM Wallet w WHERE w.id = :walletId")
    Optional<Wallet> findByIdForUpdate(@Param("walletId") String walletId);

}
