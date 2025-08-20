package com.tradexpert.request;

import com.tradexpert.domain.VerificationType;
import lombok.Data;

@Data
public class UpdatePasswordRequest {
    private String sendTo;
    private VerificationType verificationType;
}
