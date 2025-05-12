package de.jodegen.wallet.mapper;

import de.jodegen.wallet.dto.CurrencyBalanceDto;
import de.jodegen.wallet.model.CurrencyBalance;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CurrencyBalanceMapper {

    CurrencyBalanceDto toDto(CurrencyBalance currencyBalance);
}
