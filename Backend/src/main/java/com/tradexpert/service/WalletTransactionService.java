package com.tradexpert.service;

import com.tradexpert.domain.WalletTransactionType;
import com.tradexpert.model.Wallet;
import com.tradexpert.model.WalletTransaction;

import java.util.List;

public interface WalletTransactionService {
    WalletTransaction createTransaction(Wallet wallet,
                                        WalletTransactionType type,
                                        String transferId,
                                        String purpose,
                                        Long amount
    );

    List<WalletTransaction> getTransactions(Wallet wallet, WalletTransactionType type);

}
