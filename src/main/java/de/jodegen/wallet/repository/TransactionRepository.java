package de.jodegen.wallet.repository;

import de.jodegen.wallet.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
