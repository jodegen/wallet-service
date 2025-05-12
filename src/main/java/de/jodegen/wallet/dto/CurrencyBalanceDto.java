package de.jodegen.wallet.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
public class CurrencyBalanceDto {
    private Long id;
    private String currencyCode;
    private BigDecimal amount;
}
