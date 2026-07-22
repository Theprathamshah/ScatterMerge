package com.prathamcodes.scattermerge.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@Profile("dev") 
public class WireMockConfig {

    private WireMockServer wireMockServer;

    @PostConstruct
    public void startWireMock() {
        
        wireMockServer = new WireMockServer(WireMockConfiguration.options().port(9090));
        wireMockServer.start();
        WireMock.configureFor("localhost", 9090);

        log.info("Started local WireMock server on port 9090");
        configureAirlineStubs();
    }

    @PreDestroy
    public void stopWireMock() {
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.stop();
            log.info("Stopped local WireMock server");
        }
    }

    private void configureAirlineStubs() {
        
        stubFor(get(urlPathEqualTo("/api/v1/skyhigh"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withFixedDelay(150)
                .withBody("""
                    [
                      {"flightNumber": "SH-101", "airline": "SkyHigh", "origin": "JFK", "destination": "LHR", "departureTime": "2026-09-01T10:00:00", "arrivalTime": "2026-09-01T22:00:00", "price": 450.00, "currency": "USD", "flightDuration": "12h"},
                      {"flightNumber": "SH-102", "airline": "SkyHigh", "origin": "JFK", "destination": "LHR", "departureTime": "2026-09-01T18:00:00", "arrivalTime": "2026-09-02T06:00:00", "price": 480.00, "currency": "USD", "flightDuration": "12h"}
                    ]
                    """)));

        
        stubFor(get(urlPathEqualTo("/api/v1/oceanic"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withFixedDelay(320)
                .withBody("""
                    [
                      {"flightNumber": "OA-815", "airline": "Oceanic", "origin": "JFK", "destination": "LHR", "departureTime": "2026-09-01T12:00:00", "arrivalTime": "2026-09-02T00:30:00", "price": 420.00, "currency": "USD", "flightDuration": "12h 30m"}
                    ]
                    """)));

        
        stubFor(get(urlPathEqualTo("/api/v1/transglobal"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withFixedDelay(2500) 
                .withBody("""
                    [
                      {"flightNumber": "TG-202", "airline": "TransGlobal", "origin": "JFK", "destination": "LHR", "departureTime": "2026-09-01T14:00:00", "arrivalTime": "2026-09-02T02:00:00", "price": 510.00, "currency": "USD", "flightDuration": "12h"}
                    ]
                    """)));

        
        stubFor(get(urlPathEqualTo("/api/v1/starflight"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withFixedDelay(450)
                .withBody("""
                    [
                      {"flightNumber": "SF-303", "airline": "StarFlight", "origin": "JFK", "destination": "LHR", "departureTime": "2026-09-01T08:00:00", "arrivalTime": "2026-09-01T20:00:00", "price": 460.00, "currency": "USD", "flightDuration": "12h"}
                    ]
                    """)));

        
        stubFor(get(urlPathEqualTo("/api/v1/falcon"))
            .willReturn(aResponse()
                .withStatus(500) 
                .withHeader("Content-Type", "application/json")
                .withFixedDelay(100)
                .withBody("{\"error\": \"Internal Server Error\"}")));
    }
}