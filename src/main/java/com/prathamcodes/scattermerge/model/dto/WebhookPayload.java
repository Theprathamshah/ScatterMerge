package com.prathamcodes.scattermerge.model.dto;

public record WebhookPayload(
    String event,
    String timestamp,
    AggregationResponse data
) {}
