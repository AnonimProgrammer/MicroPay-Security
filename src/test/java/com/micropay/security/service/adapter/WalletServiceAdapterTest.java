package com.micropay.security.service.adapter;

import com.micropay.security.exception.InternalServiceCommunicationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WalletServiceAdapterTest {

    private WalletClient walletClient;
    private WalletServiceAdapter walletServiceAdapter;
    private static final UUID USER_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        walletClient = mock(WalletClient.class);
        walletServiceAdapter = new WalletServiceAdapter(walletClient);
    }

    @Test
    void deactivateWallet_ShouldCallClientSuccessfully() {
        walletServiceAdapter.deactivateWallet(USER_ID);
        verify(walletClient, times(1)).deactivateWallet(USER_ID);
    }

    @Test
    void activateWallet_ShouldCallClientSuccessfully() {
        walletServiceAdapter.activateWallet(USER_ID);
        verify(walletClient, times(1)).activateWallet(USER_ID);
    }

    @Test
    void closeWallet_ShouldCallClientSuccessfully() {
        walletServiceAdapter.closeWallet(USER_ID);
        verify(walletClient, times(1)).closeWallet(USER_ID);
    }

    @Test
    void getWalletId_ShouldReturnWalletIdSuccessfully() {
        when(walletClient.getWalletId(USER_ID)).thenReturn(999L);

        Long result = walletServiceAdapter.getWalletId(USER_ID);

        assertEquals(999L, result);
        verify(walletClient, times(1)).getWalletId(USER_ID);
    }

    @Test
    void fallback_ShouldThrowInternalServiceCommunicationException() {
        Throwable cause = new RuntimeException("Connection failed");

        InternalServiceCommunicationException exception = assertThrows(
                InternalServiceCommunicationException.class,
                () -> walletServiceAdapter.fallback(USER_ID, cause)
        );
        assertEquals("Wallet service is currently unavailable.", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}
