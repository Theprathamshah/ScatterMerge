package com.prathamcodes.scattermerge.model.dto;

import java.util.List;

public record AggregationResponse <T> (
    String jobId,
    String searchSummary,
    long totalExecutionTimeMs,
    int totalProvidersQueried,
    int successfulProvidersCount,
    int fallbackProvidersCount,
    List<ProviderResult<T>> providerResults
) {}
