package com.micropay.security.service.adapter;

import com.micropay.security.exception.InternalServiceCommunicationException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletServiceAdapter {

    private final WalletClient walletClient;
    private final static Logger logger = LoggerFactory.getLogger(WalletServiceAdapter.class);

    @CircuitBreaker(name = "deactivateWallet", fallbackMethod = "fallback")
    public void deactivateWallet(UUID userId) {
        logger.info("[WalletServiceAdapter] - Deactivating wallet for userId: {}", userId);

        walletClient.deactivateWallet(userId);
        logger.info("[WalletServiceAdapter] - Wallet deactivation succeeded for userId: {}", userId);
    }

    @CircuitBreaker(name = "deactivateWallet", fallbackMethod = "fallback")
    public void activateWallet(UUID userId) {
        logger.info("[WalletServiceAdapter] - Activating wallet for userId: {}", userId);

        walletClient.activateWallet(userId);
        logger.info("[WalletServiceAdapter] - Wallet activation succeeded for userId: {}", userId);
    }

    @CircuitBreaker(name = "closeWallet", fallbackMethod = "fallback")
    public void closeWallet(UUID userId) {
        logger.info("[WalletServiceAdapter] - Closing wallet for userId: {}", userId);

        walletClient.closeWallet(userId);
        logger.info("[WalletServiceAdapter] - Wallet closed successfully for userId: {}", userId);
    }

    @CircuitBreaker(name = "getWalletId", fallbackMethod = "fallback")
    public Long getWalletId(UUID userId) {
        logger.info("[WalletServiceAdapter] - Fetching wallet ID for userId: {}", userId);

        Long walletId = walletClient.getWalletId(userId);
        logger.info("[WalletServiceAdapter] - Retrieved wallet ID: {} for userId: {}", walletId, userId);
        return walletId;
    }

    public void fallback(UUID userId, Throwable throwable) {
        logger.info("[WalletServiceAdapter] - Fallback for userId: {}", userId);
        throw new InternalServiceCommunicationException("Wallet service is currently unavailable.", throwable);
    }

}
