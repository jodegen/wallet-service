package de.jodegen.wallet.service;

import de.jodegen.wallet.dto.TransactionHistoryDto;
import de.jodegen.wallet.factory.*;
import de.jodegen.wallet.mapper.TransactionHistoryMapper;
import de.jodegen.wallet.model.*;
import de.jodegen.wallet.repository.TransactionRepository;
import lombok.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionFactory transactionFactory;
    private final TransactionHistoryMapper transactionHistoryMapper;

    public List<TransactionHistoryDto> getTransactionHistory(@NonNull Wallet wallet) {
        return transactionRepository.findAllByWalletId(wallet.getId())
                .stream()
                .map(transactionHistoryMapper::toDto)
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
}
