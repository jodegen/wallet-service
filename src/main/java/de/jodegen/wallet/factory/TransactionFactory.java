package de.jodegen.wallet.factory;

import de.jodegen.wallet.model.*;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TransactionFactory {

    public Transaction createInitial(@NonNull CurrencyBalance currencyBalance) {
        return new Transaction(currencyBalance, BigDecimal.ZERO,
                TransactionType.INITIAL, TransactionReason.WALLET_CREATED);
    }

    public Transaction createOutgoingConversion(@NonNull CurrencyBalance fromBalance, @NonNull CurrencyBalance toBalance,
                                                @NonNull BigDecimal amount, @NonNull BigDecimal convertedAmount) {
        Transaction fromTransaction = new Transaction(fromBalance, amount,
                TransactionType.OUT, TransactionReason.CURRENCY_CONVERSION);
        fromTransaction.setTargetCurrencyBalance(toBalance);
        fromTransaction.setTargetAmount(convertedAmount);
        return fromTransaction;
    }

    public Transaction createIncomingConversion(@NonNull CurrencyBalance toBalance, @NonNull CurrencyBalance fromBalance,
                                                @NonNull BigDecimal amount, @NonNull BigDecimal convertedAmount) {
        Transaction transaction = new Transaction(toBalance, amount,
                TransactionType.IN, TransactionReason.CURRENCY_CONVERSION);
        transaction.setTargetCurrencyBalance(fromBalance);
        transaction.setTargetAmount(convertedAmount);
        return transaction;
    }

    public Transaction createBuyNowPurchase(@NonNull CurrencyBalance currencyBalance, @NonNull BigDecimal amount, long auctionId) {
        Transaction transaction = new Transaction(currencyBalance, amount,
                TransactionType.OUT, TransactionReason.AUCTION_BUY_NOW);
        transaction.setReferenceId(auctionId);
        return transaction;
    }

    public Transaction createBidPlaced(@NonNull CurrencyBalance currencyBalance, @NonNull BigDecimal amount, long auctionId) {
        Transaction transaction = new Transaction(currencyBalance, amount,
                TransactionType.OUT, TransactionReason.AUCTION_BID);
        transaction.setReferenceId(auctionId);
        return transaction;
    }

    public Transaction createBidCancelled(@NonNull CurrencyBalance currencyBalance, @NonNull BigDecimal amount, long auctionId) {
        Transaction transaction = new Transaction(currencyBalance, amount.negate(),
                TransactionType.IN, TransactionReason.AUCTION_BID_CANCELLED);
        transaction.setReferenceId(auctionId);
        return transaction;
    }

    public Transaction createAuctionPayout(@NonNull CurrencyBalance currencyBalance, @NonNull BigDecimal amount, long auctionId, boolean buyNow) {
        Transaction transaction = new Transaction(currencyBalance, amount.negate(),
                TransactionType.IN,
                buyNow ? TransactionReason.AUCTION_SALE_BUY_NOW : TransactionReason.AUCTION_SALE);
        transaction.setReferenceId(auctionId);
        return transaction;
    }
}
