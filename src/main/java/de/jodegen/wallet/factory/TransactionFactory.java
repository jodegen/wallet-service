package de.jodegen.wallet.factory;

import de.jodegen.wallet.model.*;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TransactionFactory {

    public Transaction createInitial(@NonNull Wallet wallet) {
        return new Transaction(wallet, WalletFactory.DEFAULT_CURRENCY, BigDecimal.ZERO,
                TransactionType.INITIAL, TransactionReason.WALLET_CREATED);
    }

    public Transaction createConversion(@NonNull Wallet wallet, @NonNull String fromCurrency, @NonNull String toCurrency,
                                        @NonNull BigDecimal amount, @NonNull BigDecimal convertedAmount) {
        Transaction transaction = new Transaction(wallet, fromCurrency, amount,
                TransactionType.CONVERSION, TransactionReason.CURRENCY_CONVERSION);
        transaction.setTargetWalletId(wallet.getId());
        transaction.setTargetCurrencyCode(toCurrency);
        transaction.setTargetAmount(convertedAmount);
        return transaction;
    }

    public Transaction createBuyNowPurchase(@NonNull Wallet wallet, @NonNull String currencyCode, @NonNull BigDecimal amount, long auctionId) {
        Transaction transaction = new Transaction(wallet, currencyCode, amount,
                TransactionType.OUT, TransactionReason.AUCTION_BUY_NOW);
        transaction.setReferenceId(auctionId);
        return transaction;
    }

    public Transaction createBidPlaced(@NonNull Wallet wallet, @NonNull String currencyCode, @NonNull BigDecimal amount, long auctionId) {
        Transaction transaction = new Transaction(wallet, currencyCode, amount,
                TransactionType.OUT, TransactionReason.AUCTION_BID);
        transaction.setReferenceId(auctionId);
        return transaction;
    }

    public Transaction createBidCancelled(@NonNull Wallet wallet, @NonNull String currencyCode, @NonNull BigDecimal amount, long auctionId) {
        Transaction transaction = new Transaction(wallet, currencyCode, amount.negate(),
                TransactionType.IN, TransactionReason.AUCTION_BID_CANCELLED);
        transaction.setReferenceId(auctionId);
        return transaction;
    }

    public Transaction createAuctionPayout(@NonNull Wallet wallet, @NonNull String currencyCode, @NonNull BigDecimal amount, long auctionId, boolean buyNow) {
        Transaction transaction = new Transaction(wallet, currencyCode, amount.negate(),
                TransactionType.IN,
                buyNow ? TransactionReason.AUCTION_SALE_BUY_NOW : TransactionReason.AUCTION_SALE);
        transaction.setReferenceId(auctionId);
        return transaction;
    }
}
