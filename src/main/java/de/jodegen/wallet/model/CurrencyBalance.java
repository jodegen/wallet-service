package de.jodegen.wallet.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "currency_balance")
public class CurrencyBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @Column(name = "currency_code", nullable = false)
    private String currencyCode;

    @Column(name = "amount", nullable = false, precision = 19, scale = 6)
    private BigDecimal amount;

    public CurrencyBalance(@NonNull Wallet wallet, @NonNull String currencyCode, @NonNull BigDecimal amount) {
        this.wallet = wallet;
        this.currencyCode = currencyCode;
        this.amount = amount;
    }

    public void decreaseAmount(@NonNull BigDecimal amount) {
        this.amount = this.amount.subtract(amount);
    }

    public void increaseAmount(@NonNull BigDecimal amount) {
        this.amount = this.amount.add(amount);
    }

    public boolean isEmpty() {
        return amount.compareTo(BigDecimal.ZERO) == 0;
    }
}
