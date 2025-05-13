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

    @Column(name = "reserved_amount", nullable = false, precision = 19, scale = 6)
    private BigDecimal reservedAmount = BigDecimal.ZERO;

    public void decreaseAmount(@NonNull BigDecimal amount) {
        this.amount = this.amount.subtract(amount);
    }

    public void increaseAmount(@NonNull BigDecimal amount) {
        this.amount = this.amount.add(amount);
    }

    public boolean isEmpty() {
        return amount.compareTo(BigDecimal.ZERO) == 0;
    }

    public boolean hasReservedAmount() {
        return reservedAmount.compareTo(BigDecimal.ZERO) > 0;
    }

    public void reserveAmount(@NonNull BigDecimal amount) {
        if (this.amount.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance to reserve the amount");
        }
        this.amount = this.amount.subtract(amount);
        this.reservedAmount = this.reservedAmount.add(amount);
    }

    public void releaseReservedAmount(@NonNull BigDecimal amount) {
        if (this.reservedAmount.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient reserved amount to release");
        }
        this.reservedAmount = this.reservedAmount.subtract(amount);
        this.amount = this.amount.add(amount);
    }
}
