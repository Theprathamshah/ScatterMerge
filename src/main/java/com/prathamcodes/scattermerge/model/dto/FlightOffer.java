package com.prathamcodes.scattermerge.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FlightOffer(
    String flightNumber,
    String airline,
    String origin,
    String destination,
    LocalDateTime departureTime,
    LocalDateTime arrivalTime,
    BigDecimal price,
    String currency,
    String flightDuration
) {}
