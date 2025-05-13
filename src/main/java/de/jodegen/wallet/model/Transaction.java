package de.jodegen.wallet.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "wallet_id", nullable = false)
    private Long walletId;

    @Column(name = "currency_code", nullable = false)
    private String currencyCode;

    @Column(name = "amount", nullable = false, precision = 19, scale = 6)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason")
    private TransactionReason reason;

    @Column(name = "target_wallet_id")
    private Long targetWalletId;

    @Column(name = "target_currency")
    private String targetCurrencyCode;

    @Column(name = "target_amount", precision = 19, scale = 6)
    private BigDecimal targetAmount;

    @Column(name = "reference_id")
    private Long referenceId;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    public Transaction(@NonNull Wallet wallet,
                       @NonNull String currencyCode,
                       @NonNull BigDecimal amount,
                       @NonNull TransactionType type,
                       @NonNull TransactionReason reason) {
        this.walletId = wallet.getId();
        this.currencyCode = currencyCode;
        this.amount = amount;
        this.type = type;
        this.reason = reason;
    }
}
