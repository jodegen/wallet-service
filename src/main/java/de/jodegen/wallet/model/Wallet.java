package de.jodegen.wallet.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "wallet")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CurrencyBalance> balances = new ArrayList<>();

    public Wallet(@NonNull Long userId) {
        this.userId = userId;
    }

    public Optional<CurrencyBalance> getBalance(String currencyCode) {
        return balances.stream()
                .filter(balance -> balance.getCurrencyCode().equals(currencyCode))
                .findFirst();
    }
}
