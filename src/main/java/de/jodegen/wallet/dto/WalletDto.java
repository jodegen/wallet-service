package de.jodegen.wallet.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
public class WalletDto {
    private Long id;
    private Long userId;
    private List<CurrencyBalanceDto> balances;
}
