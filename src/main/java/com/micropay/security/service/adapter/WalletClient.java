package com.micropay.security.service.adapter;

import com.micropay.security.config.SystemConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(
        name = "wallet-service",
        url = SystemConfiguration.WALLET_SERVICE_URL
)
public interface WalletClient {

    @PutMapping("/deactivate")
    ResponseEntity<Void> deactivateWallet(@RequestHeader("X-User-Id") UUID userId);

    @PutMapping("/activate")
    ResponseEntity<Void> activateWallet(@RequestHeader("X-User-Id") UUID userId);

    @PutMapping("/close")
    ResponseEntity<Void> closeWallet(@RequestHeader("X-User-Id") UUID userId);

    @GetMapping()
    Long getWalletId(@RequestHeader("X-User-Id") UUID userId);

}
