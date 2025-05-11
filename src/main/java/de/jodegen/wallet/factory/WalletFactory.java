package de.jodegen.wallet.factory;

import de.jodegen.wallet.model.*;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Component
public class WalletFactory {

    public static final String DEFAULT_CURRENCY = "EUR";

    public Wallet createWallet(@NonNull Long userId) {
        Wallet wallet = new Wallet();
        wallet.setUserId(userId);

        CurrencyBalance euroBalance = new CurrencyBalance();
        euroBalance.setCurrencyCode(DEFAULT_CURRENCY);
        euroBalance.setAmount(BigDecimal.ZERO);
        euroBalance.setWallet(wallet);

        wallet.setBalances(new ArrayList<>(List.of(euroBalance)));
        return wallet;
    }
}
