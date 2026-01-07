package com.hamid.digital_wallet.integration;

import com.hamid.digital_wallet.entity.User;
import com.hamid.digital_wallet.entity.Wallet;
import com.hamid.digital_wallet.repository.UserRepository;
import com.hamid.digital_wallet.service.WalletService;
import com.hamid.digital_wallet.repository.WalletRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cglib.core.Local;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class WalletServiceIT {

    @Autowired
    UserRepository userRepository;

    @Autowired
    WalletService walletService;

    @Autowired
    WalletRepository walletRepository;

    @Test
    void shouldTransferMoneyCorrectly() {

        // ---- USER 1 ----
        User user1 = new User();
        user1.setName("UserOne");
        user1.setEmail("user1@test.com");
        user1.setPasswordHash("12345");
//        user1.setPhone("12345");
        user1.setStatus("ACTIVE");
        user1.setCreatedAt(LocalDateTime.now());
        user1.setUpdatedAt(LocalDateTime.now());
        user1.setWallet(null);  // FIX THIS TEST CASE.

        user1 = userRepository.saveAndFlush(user1);

        Wallet fromWallet = new Wallet();
        fromWallet.setUser(user1);
        fromWallet.setBalance(new BigDecimal("1000"));
        fromWallet.setCreatedAt(LocalDateTime.now());
        walletRepository.saveAndFlush(fromWallet);

        user1.setWallet(fromWallet);
        userRepository.saveAndFlush(user1);

        // ---- USER 2 ----
        User user2 = new User();
        user2.setName("UserTwo");
        user2.setEmail("user2@test.com");
        user2.setPasswordHash("54321");
        user2.setStatus("ACTIVE");
        user2.setCreatedAt(LocalDateTime.now());
        user2.setUpdatedAt(LocalDateTime.now());
        user2.setWallet(null);

        user2 = userRepository.saveAndFlush(user2);

        Wallet toWallet = new Wallet();
        toWallet.setUser(user2);
        toWallet.setBalance(BigDecimal.ZERO);
        toWallet.setCreatedAt(LocalDateTime.now());
        walletRepository.saveAndFlush(toWallet);

        user2.setWallet(toWallet);
        userRepository.saveAndFlush(user2);

        // ---- TRANSFER ----
        walletService.transfer(
                fromWallet.getId(),
                toWallet.getId(),
                new BigDecimal("300"),
                "REF_001"
        );

        Wallet updatedFrom = walletRepository.findById(fromWallet.getId()).orElseThrow();
        Wallet updatedTo = walletRepository.findById(toWallet.getId()).orElseThrow();

        System.out.println("Updated FROM wallet :" + updatedFrom.getUser().getWallet() + " and value is : " + updatedFrom.getBalance());
        System.out.println("Updated TO wallet :" + updatedTo.getUser().getWallet() + " and value is : " + updatedTo.getBalance());
//        System.out.println("Updated to value : " + updatedTo.getBalance());

        assertEquals(new BigDecimal("700"), updatedFrom.getBalance());
        assertEquals(new BigDecimal("300"), updatedTo.getBalance());
    }

}
