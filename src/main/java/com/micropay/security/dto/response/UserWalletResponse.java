package com.micropay.security.dto.response;

import java.util.UUID;

public record UserWalletResponse(
        UUID userId,
        Long walletId
) {}
