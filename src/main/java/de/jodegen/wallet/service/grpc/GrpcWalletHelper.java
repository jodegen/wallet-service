package de.jodegen.wallet.service.grpc;

import de.jodegen.wallet.model.*;
import de.jodegen.wallet.service.WalletService;
import io.grpc.Status;
import lombok.*;
import org.springframework.stereotype.Component;

import java.math.*;

@Component
@RequiredArgsConstructor
public class GrpcWalletHelper {

    private final WalletService walletService;

    public CurrencyBalance getValidBalance(long userId, @NonNull String currencyCode) {
        Wallet wallet = walletService.findWalletByUserId(userId);
        return wallet.getBalance(currencyCode).orElseThrow(() ->
                Status.NOT_FOUND
                        .withDescription("No balance found for currency: " + currencyCode)
                        .asRuntimeException()
        );
    }

    public void ensureSufficientBalance(@NonNull CurrencyBalance balance, double requiredAmount) {
        if (balance.getAmount().compareTo(BigDecimal.valueOf(requiredAmount)) < 0) {
            throw Status.FAILED_PRECONDITION
                    .withDescription("Insufficient balance for currency: " + balance.getCurrencyCode())
                    .asRuntimeException();
        }
    }

    public BigDecimal toBigDecimal(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
    }
}
