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

    public void createInitialTransaction(@NonNull Wallet wallet) {
        Transaction tx = transactionFactory.createInitial(wallet);
        transactionRepository.save(tx);
    }

    public void createConversionTransaction(@NonNull Wallet wallet, @NonNull String fromCurrency,
                                            @NonNull String toCurrency, @NonNull BigDecimal amount,
                                            @NonNull BigDecimal convertedAmount) {
        Transaction tx = transactionFactory.createConversion(wallet, fromCurrency, toCurrency, amount, convertedAmount);
        transactionRepository.save(tx);
    }

    public void createPurchaseTransaction(@NonNull Wallet wallet, @NonNull String currencyCode,
                                          @NonNull BigDecimal amount, long auctionId) {
        Transaction tx = transactionFactory.createBuyNowPurchase(wallet, currencyCode, amount, auctionId);
        transactionRepository.save(tx);
    }

    public void createBidPlacedTransaction(@NonNull Wallet wallet, @NonNull String currencyCode,
                                           @NonNull BigDecimal amount, long auctionId) {
        Transaction tx = transactionFactory.createBidPlaced(wallet, currencyCode, amount, auctionId);
        transactionRepository.save(tx);
    }

    public void createBidCancelledTransaction(@NonNull Wallet wallet, @NonNull String currencyCode,
                                              @NonNull BigDecimal amount, long auctionId) {
        Transaction tx = transactionFactory.createBidCancelled(wallet, currencyCode, amount, auctionId);
        transactionRepository.save(tx);
    }

    public void createPayoutTransaction(@NonNull Wallet wallet, @NonNull String currencyCode, @NonNull BigDecimal amount, long auctionId, boolean buyNow) {
        Transaction tx = transactionFactory.createAuctionPayout(wallet, currencyCode, amount, auctionId, buyNow);
        transactionRepository.save(tx);
    }
}
