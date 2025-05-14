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

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "currency_balance_id", nullable = false)
    private CurrencyBalance currencyBalance;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_currency_balance_id")
    private CurrencyBalance targetCurrencyBalance;

    @Column(name = "target_amount", precision = 19, scale = 6)
    private BigDecimal targetAmount;

    @Column(name = "reference_id")
    private Long referenceId;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    public Transaction(@NonNull CurrencyBalance currencyBalance,
                       @NonNull BigDecimal amount,
                       @NonNull TransactionType type,
                       @NonNull TransactionReason reason) {
        this.currencyBalance = currencyBalance;
        this.amount = amount;
        this.type = type;
        this.reason = reason;
    }
}
