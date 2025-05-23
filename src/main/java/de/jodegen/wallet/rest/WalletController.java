package de.jodegen.wallet.rest;

import de.jodegen.wallet.dto.*;
import de.jodegen.wallet.factory.TransactionFactory;
import de.jodegen.wallet.model.*;
import de.jodegen.wallet.service.*;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/wallet")
public class WalletController {

    private final WalletService walletService;
    private final SecurityService securityService;
    private final TransactionService transactionService;
    private final TransactionFactory transactionFactory;

    @GetMapping
    public WalletDto getWallet() {
        JwtUserDetails jwtUserDetails = securityService.assertLoggedInUserAccount();
        return walletService.getWalletByUserId(jwtUserDetails.getUserId());
    }

    @PostMapping
    public ResponseEntity<WalletDto> createWallet() {
        JwtUserDetails jwtUserDetails = securityService.assertLoggedInUserAccount();
        return ResponseEntity.ok(walletService.createWallet(jwtUserDetails.getUserId()));
    }

    @PostMapping(path = "/conversion")
    public ResponseEntity<Boolean> conversion(@RequestBody ConversionRequestDto conversionRequestDto) {
        JwtUserDetails jwtUserDetails = securityService.assertLoggedInUserAccount();
        try {
            walletService.convertCurrency(jwtUserDetails.getUserId(), conversionRequestDto);
            return ResponseEntity.ok(true);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(false);
        }
    }

    @GetMapping("/transactions/{currencyCode}")
    public List<TransactionHistoryDto> getBalanceHistory(@PathVariable String currencyCode) {
        JwtUserDetails jwtUserDetails = securityService.assertLoggedInUserAccount();
        return transactionService.getTransactionHistory(jwtUserDetails.getUserId(), currencyCode);
    }
}
