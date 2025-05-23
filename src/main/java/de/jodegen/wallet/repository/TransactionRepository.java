package de.jodegen.wallet.repository;

import de.jodegen.wallet.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByCurrencyBalance(CurrencyBalance currencyBalance);
}
