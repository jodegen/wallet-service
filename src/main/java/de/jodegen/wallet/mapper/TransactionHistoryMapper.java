package de.jodegen.wallet.mapper;

import de.jodegen.wallet.dto.TransactionHistoryDto;
import de.jodegen.wallet.model.Transaction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionHistoryMapper {

    TransactionHistoryDto toDto(Transaction transaction);
}
