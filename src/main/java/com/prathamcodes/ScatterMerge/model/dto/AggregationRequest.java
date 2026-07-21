package com.prathamcodes.scattermerge.model.dto;

public record AggregationRequest(
    String origin,
    String destination,
    String departureDate,
    String webhookUrl,
    String clientSecret
) {}
