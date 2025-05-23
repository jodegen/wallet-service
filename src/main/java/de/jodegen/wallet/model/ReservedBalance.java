package de.jodegen.wallet.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "reserved_balance")
public class ReservedBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "currency_balance_id", nullable = false)
    private CurrencyBalance referenceBalance;

    @Column(name = "auction_id")
    private Long auctionId;

    @Column(name = "amount", nullable = false, precision = 19, scale = 6)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "reserve_reason", nullable = false)
    private ReserveReason reason;
}
