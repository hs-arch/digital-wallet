package com.hamid.digital_wallet.repository;

import com.hamid.digital_wallet.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferRepository extends JpaRepository<Transfer, String> {
}
