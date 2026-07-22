package com.prathamcodes.scattermerge.config;


import java.net.http.HttpClient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.prathamcodes.scattermerge.model.dto.FlightOffer;
import com.prathamcodes.scattermerge.service.provider.AirlineProviderClient;
import com.prathamcodes.scattermerge.service.provider.ProviderConnector;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class ProviderConfig {

    @Bean
    public ProviderConnector<FlightOffer> skyHighConnector(HttpClient httpClient, ObjectMapper objectMapper) {
        return new AirlineProviderClient(httpClient, "SkyHigh Airways", "http://localhost:9090/api/v1/skyhigh", "skyhigh", objectMapper);
    }

    @Bean
    public ProviderConnector<FlightOffer> oceanicConnector(HttpClient httpClient, ObjectMapper objectMapper) {
        return new AirlineProviderClient(httpClient, "Oceanic Air", "http://localhost:9090/api/v1/oceanic", "oceanic", objectMapper);
    }

    @Bean
    public ProviderConnector<FlightOffer> transGlobalConnector(HttpClient httpClient, ObjectMapper objectMapper) {
        return new AirlineProviderClient(httpClient, "TransGlobal Jets", "http://localhost:9090/api/v1/transglobal", "transglobal", objectMapper);
    }

    @Bean
    public ProviderConnector<FlightOffer> starFlightConnector(HttpClient httpClient, ObjectMapper objectMapper) {
        return new AirlineProviderClient(httpClient, "StarFlight", "http://localhost:9090/api/v1/starflight", "starflight", objectMapper);
    }

    @Bean
    public ProviderConnector<FlightOffer> falconConnector(HttpClient httpClient, ObjectMapper objectMapper) {
        return new AirlineProviderClient(httpClient, "Falcon Express", "http://localhost:9090/api/v1/falcon", "falcon", objectMapper);
    }
}