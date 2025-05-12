package de.jodegen.wallet.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
public class ConversionRequestDto {
    private String fromCurrency;
    private String toCurrency;
    private BigDecimal amount;
}
