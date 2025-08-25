package de.jodegen.wallet.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
public class AddFundsRequestDto {
    private BigDecimal amount;
    private String currencyCode;
}
