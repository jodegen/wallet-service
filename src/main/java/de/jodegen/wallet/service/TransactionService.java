package de.jodegen.wallet.service;

import de.jodegen.wallet.dto.TransactionHistoryDto;
import de.jodegen.wallet.factory.*;
import de.jodegen.wallet.mapper.TransactionHistoryMapper;
import de.jodegen.wallet.model.*;
import de.jodegen.wallet.repository.*;
import lombok.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionFactory transactionFactory;
    private final TransactionHistoryMapper transactionHistoryMapper;
    private final WalletRepository walletRepository;

    public List<TransactionHistoryDto> getTransactionHistory(@NonNull Long userId, @NonNull String currencyCode) {
        try {
            Currency.getInstance(currencyCode);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid currency code: " + currencyCode, e);
        }

        Optional<Wallet> optionalWallet = walletRepository.findByUserId(userId);
        if (optionalWallet.isEmpty()) {
            throw new IllegalArgumentException("No wallet found for user ID: " + userId);
        }

        Wallet wallet = optionalWallet.get();
        CurrencyBalance balance = wallet.getBalances()
                .stream()
                .filter(cb -> cb.getCurrencyCode().equals(currencyCode))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No currency balance found for currency code: " + currencyCode));

        return transactionRepository.findByCurrencyBalance(balance)
                .stream()
                .map(transactionHistoryMapper::toDto)
                .sorted(Comparator.comparing(TransactionHistoryDto::getTimestamp).reversed())
                .toList();
    }

    public void createInitialTransaction(@NonNull CurrencyBalance currencyBalance) {
        Transaction tx = transactionFactory.createInitial(currencyBalance);
        transactionRepository.save(tx);
    }

    public void createConversionTransaction(@NonNull CurrencyBalance fromBalance, @NonNull CurrencyBalance toBalance,
                                            @NonNull BigDecimal amount, @NonNull BigDecimal convertedAmount) {
        if (fromBalance.getCurrencyCode().equals(toBalance.getCurrencyCode())) {
            throw new IllegalArgumentException("Cannot convert the same currency");
        }

        Transaction tx = transactionFactory.createOutgoingConversion(fromBalance, toBalance, amount, convertedAmount);
        transactionRepository.save(tx);

        Transaction tx2 = transactionFactory.createIncomingConversion(toBalance, fromBalance, convertedAmount, amount);
        transactionRepository.save(tx2);
    }

    public void createPurchaseTransaction(@NonNull CurrencyBalance currencyBalance, @NonNull BigDecimal amount, long auctionId) {
        Transaction tx = transactionFactory.createBuyNowPurchase(currencyBalance, amount, auctionId);
        transactionRepository.save(tx);
    }

    public void createBidPlacedTransaction(@NonNull CurrencyBalance currencyBalance, @NonNull BigDecimal amount, long auctionId) {
        Transaction tx = transactionFactory.createBidPlaced(currencyBalance, amount, auctionId);
        transactionRepository.save(tx);
    }

    public void createBidCancelledTransaction(@NonNull CurrencyBalance currencyBalance, @NonNull BigDecimal amount, long auctionId) {
        Transaction tx = transactionFactory.createBidCancelled(currencyBalance, amount, auctionId);
        transactionRepository.save(tx);
    }

    public void createPayoutTransaction(@NonNull CurrencyBalance currencyBalance, @NonNull BigDecimal amount, long auctionId, boolean buyNow) {
        Transaction tx = transactionFactory.createAuctionPayout(currencyBalance, amount, auctionId, buyNow);
        transactionRepository.save(tx);
    }

    public void createAddFundsTransaction(@NonNull CurrencyBalance balance, @NonNull BigDecimal amount) {
        Transaction tx = transactionFactory.createAddFunds(balance, amount);
        transactionRepository.save(tx);
    }
}
