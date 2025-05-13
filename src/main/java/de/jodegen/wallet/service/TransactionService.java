package de.jodegen.wallet.service;

import de.jodegen.wallet.factory.WalletFactory;
import de.jodegen.wallet.model.*;
import de.jodegen.wallet.repository.TransactionRepository;
import lombok.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public void createInitialTransaction(@NonNull Wallet wallet) {
        Transaction transaction = new Transaction(wallet, WalletFactory.DEFAULT_CURRENCY, BigDecimal.ZERO, TransactionType.INITIAL);
        transactionRepository.save(transaction);
    }

    public void createConversionTransaction(@NonNull Wallet wallet, @NonNull String fromCurrency,
                                            @NonNull String toCurrency, @NonNull BigDecimal amount,
                                            @NonNull BigDecimal convertedAmount) {
        Transaction transaction = new Transaction(wallet, fromCurrency, amount, TransactionType.CONVERSION);
        transaction.setTargetWalletId(wallet.getId());
        transaction.setTargetCurrencyCode(toCurrency);
        transaction.setTargetAmount(convertedAmount);
        transactionRepository.save(transaction);
    }

    public void createPurchaseTransaction(@NonNull Wallet wallet, @NonNull String currencyCode,
                                            @NonNull BigDecimal amount, long auctionId) {
        Transaction transaction = new Transaction(wallet, currencyCode, amount, TransactionType.PAYMENT);
        transaction.setReferenceId(String.valueOf(auctionId));
        transactionRepository.save(transaction);
    }

    public void createBidPlacedTransaction(@NonNull Wallet wallet, @NonNull String currencyCode,
                                            @NonNull BigDecimal amount, long auctionId) {
        Transaction transaction = new Transaction(wallet, currencyCode, amount, TransactionType.BID_PLACED);
        transaction.setReferenceId(String.valueOf(auctionId));
        transactionRepository.save(transaction);
    }

    public void createBidCancelledTransaction(@NonNull Wallet wallet, @NonNull String currencyCode,
                                            @NonNull BigDecimal amount, long auctionId) {
        Transaction transaction = new Transaction(wallet, currencyCode, amount.negate(), TransactionType.BID_CANCELLED);
        transaction.setReferenceId(String.valueOf(auctionId));
        transactionRepository.save(transaction);
    }
}
