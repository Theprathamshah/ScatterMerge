package com.prathamcodes.scattermerge.service.provider;


import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Collections;
import java.util.List;

import com.prathamcodes.scattermerge.model.dto.AggregationRequest;
import com.prathamcodes.scattermerge.model.dto.FlightOffer;
import com.prathamcodes.scattermerge.model.dto.ProviderResult;
import com.prathamcodes.scattermerge.model.enums.ProviderStatus;

import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
public class AirlineProviderClient implements ProviderConnector<FlightOffer> {

    private final HttpClient httpClient;
    private final String providerId;
    private final String providerName;
    private final String baseUrl;
    private final ObjectMapper objectMapper;

    public AirlineProviderClient(HttpClient httpClient, String providerId, String providerName, String baseUrl, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.providerId = providerId;
        this.providerName = providerName;
        this.baseUrl = baseUrl;
        this.objectMapper = objectMapper;
    }
	@Override
	public String getProviderId() {
        return providerId;
	}

	@Override
	public String getProviderName() {
        return providerName;
	}

	@Override
	public ProviderResult<FlightOffer> fetchOffers(AggregationRequest request) {
		long startTime = System.currentTimeMillis();
        
        String targetUrl = String.format("%s?origin=%s&destination=%s&departureDate=%s",
                baseUrl, request.origin(), request.destination(), request.departureDate());
        try {
            
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(targetUrl))
                    .timeout(Duration.ofSeconds(4)) 
                    .header("Accept", "application/json")
                    .GET()
                    .build();
            
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            long latency = System.currentTimeMillis() - startTime;
            
            if (response.statusCode() != 200) {
                log.error("Provider {} returned HTTP error: {}", providerName, response.statusCode());
                return new ProviderResult<>(providerId, providerName, ProviderStatus.FAILED_FALLBACK, latency,
                        Collections.emptyList(), "HTTP " + response.statusCode());
            }
            
            List<FlightOffer> offers = objectMapper.readValue(response.body(), new TypeReference<>() {});
            return new ProviderResult<>(providerId, providerName, ProviderStatus.SUCCESS, latency, offers, null);
        } catch (IOException | InterruptedException e) {
            long latency = System.currentTimeMillis() - startTime;
            log.error("Error fetching offers from provider {}: {}", providerName, e.getMessage());
            return new ProviderResult<>(providerId, providerName, ProviderStatus.FAILED_FALLBACK, latency,
                    Collections.emptyList(), e.getMessage());
        }
	}

}
