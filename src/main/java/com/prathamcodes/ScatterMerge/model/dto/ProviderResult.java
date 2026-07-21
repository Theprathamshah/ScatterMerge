package com.prathamcodes.scattermerge.model.dto;

import com.prathamcodes.scattermerge.model.enums.ProviderStatus;
import java.math.BigDecimal;
import java.util.List;

public record ProviderResult(
    String providerId,
    String providerName,
    ProviderStatus status,
    long latencyMs,
    List<FlightOffer> offers,
    String errorMessage
) {
    public record FlightOffer(
        String flightNumber,
        BigDecimal price,
        String currency,
        String flightDuration
    ) {}
}
