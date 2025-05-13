package de.jodegen.wallet.repository;

import de.jodegen.wallet.model.CurrencyBalance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyBalanceRepository extends JpaRepository<CurrencyBalance, Long> {
}
