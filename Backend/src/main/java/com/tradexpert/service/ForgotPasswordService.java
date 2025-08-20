package com.tradexpert.service;

import com.tradexpert.domain.VerificationType;
import com.tradexpert.model.ForgotPasswordToken;
import com.tradexpert.model.User;

public interface ForgotPasswordService {

    ForgotPasswordToken createToken(User user, String id, String otp,
                                    VerificationType verificationType,String sendTo);

    ForgotPasswordToken findById(String id);

    ForgotPasswordToken findByUser(Long userId);

    void deleteToken(ForgotPasswordToken token);

    boolean verifyToken(ForgotPasswordToken token,String otp);
}
