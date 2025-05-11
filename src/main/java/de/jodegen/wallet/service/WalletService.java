package de.jodegen.wallet.service;

import de.jodegen.wallet.factory.WalletFactory;
import de.jodegen.wallet.model.*;
import de.jodegen.wallet.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final ExchangeGrpcClient exchangeGrpcClient;
    private final WalletFactory walletFactory;
    private final TransactionService transactionService;

    public Wallet createWallet(@NonNull Long userId) {
        if (walletRepository.findByUserId(userId).isPresent()) {
            throw new IllegalArgumentException("Wallet already exists for user: " + userId);
        }
        Wallet wallet = walletFactory.createWallet(userId);
        Wallet persistedWallet = walletRepository.save(wallet);
        transactionService.createInitialTransaction(persistedWallet);
        return persistedWallet;
    }

    public Wallet findWalletByUserId(@NonNull Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found for user: " + userId));
    }

    public List<String> listAllBalances(@NonNull Long userId) {
        Wallet wallet = findWalletByUserId(userId);
        return wallet.getBalances()
                .stream()
                .map(CurrencyBalance::getCurrencyCode)
                .sorted()
                .toList();
    }

    @Transactional
    public void convertCurrency(Long userId, String fromCurrency, String toCurrency, BigDecimal amount) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found"));

        CurrencyBalance fromBalance = wallet.getBalance(fromCurrency)
                .orElseThrow(() -> new IllegalArgumentException("No balance in currency: " + fromCurrency));

        CurrencyBalance toBalance = wallet.getBalance(toCurrency)
                .orElseThrow(() -> new IllegalArgumentException("No balance in currency: " + toCurrency));

        if (fromBalance.getAmount().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds in " + fromCurrency);
        }

        BigDecimal convertedAmount = exchangeGrpcClient.convert(fromCurrency, toCurrency, amount);
        if (convertedAmount == null) {
            throw new IllegalArgumentException("Conversion failed");
        }
        fromBalance.decreaseAmount(amount);
        toBalance.increaseAmount(convertedAmount);

        walletRepository.save(wallet);
        transactionService.createConversionTransaction(wallet, fromCurrency, toCurrency, amount, convertedAmount);
    }
}
