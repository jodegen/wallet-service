package de.jodegen.wallet.rest;

import de.jodegen.wallet.dto.WalletDto;
import de.jodegen.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/v1/wallet")
public class WalletController {

    private final WalletService walletService;

    @GetMapping
    public WalletDto getWallet() {
        return null;
    }
}
