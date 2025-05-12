package de.jodegen.wallet.mapper;

import de.jodegen.wallet.dto.WalletDto;
import de.jodegen.wallet.model.Wallet;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {CurrencyBalanceMapper.class})
public interface WalletMapper {

    WalletDto toDto(Wallet wallet);
}
