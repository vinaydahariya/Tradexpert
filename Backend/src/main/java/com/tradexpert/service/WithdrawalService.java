package com.tradexpert.service;

import com.tradexpert.model.User;
import com.tradexpert.model.Withdrawal;

import java.util.List;

public interface WithdrawalService {

    Withdrawal requestWithdrawal(Long amount,User user);
    Withdrawal procedWithdrawal(Long withdrawalId,boolean accept) throws Exception;
    List<Withdrawal> getUsersWithdrawalHistory(User user);
    List<Withdrawal> getAllWithdrawalRequest();
}
