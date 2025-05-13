package de.jodegen.wallet.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class TransactionHistoryDto {
    private Long id;
    private LocalDateTime timestamp;
    private String currencyCode;
    private BigDecimal amount;
    private String type;
    private String reason;
    private String reasonLabel;
    private String referenceId;
}

