package de.jodegen.wallet.service;

import de.jodegen.wallet.model.*;
import de.jodegen.wallet.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final ExchangeGrpcClient exchangeGrpcClient;
    private final TransactionRepository transactionRepository;

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

        Transaction transaction = new Transaction(wallet, fromCurrency, amount, TransactionType.CONVERSION);
        transaction.setTargetWalletId(wallet.getId());
        transaction.setTargetCurrencyCode(toCurrency);
        transaction.setTargetAmount(convertedAmount);
        transactionRepository.save(transaction);
    }
}
