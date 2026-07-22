package com.prathamcodes.scattermerge.model.dto;

import java.util.List;

import com.prathamcodes.scattermerge.model.enums.ProviderStatus;

public record ProviderResult<T>(
    String providerId,
    String providerName,
    ProviderStatus status,
    long latencyMs,
    List<T> offers,
    String errorMessage
) {
}
