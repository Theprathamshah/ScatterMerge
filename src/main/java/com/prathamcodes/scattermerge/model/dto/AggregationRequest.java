package com.prathamcodes.scattermerge.model.dto;

import org.hibernate.validator.constraints.URL;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;

public record AggregationRequest(
    @NotBlank(message = "Origin cannot be blank")
    String origin,
    @NotBlank(message = "Destination cannot be blank")
    String destination,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotBlank(message = "Departure date cannot be blank")
    String departureDate,
    @NotBlank(message = "Webhook URL cannot be blank")
    @URL(message = "Webhook URL must be a valid URL")
    String webhookUrl,
    String clientSecret
) {}
