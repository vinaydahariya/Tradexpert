package com.tradexpert.model;

import com.tradexpert.domain.VerificationType;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
public class TwoFactorAuth {

    private boolean isEnabled = false;
    private VerificationType sendTo;
}
