# ScatterMerge: Daily Implementation Roadmap (Approach B - Generic Aggregation)

This document outlines a detailed 20-day plan to build, test, and polish the generic **ScatterMerge Asynchronous API Aggregator & Webhook Dispatcher**. Assuming **2 hours daily** of focused effort, you can check off each day as you achieve the validation goals.

---

## Phase 1: Domain & Storage Foundation (Days 1–3)

### Day 1: Domain Models, DTOs, and Enums
* **Goal**: Establish core generic data contracts representing requests, provider responses, and internal job tracking.
* **Tasks**:
  * Implement the following records in `com.prathamcodes.scattermerge.model.dto`:
    * [AggregationRequest](file:///Users/prathamshah/Desktop/Work/Personal/ScatterMerge/src/main/java/com/prathamcodes/ScatterMerge/model/dto/AggregationRequest.java): Query parameters (origin, destination, departureDate), client webhook URL, and optional client secret.
    * [ProviderResult<T>](file:///Users/prathamshah/Desktop/Work/Personal/ScatterMerge/src/main/java/com/prathamcodes/ScatterMerge/model/dto/ProviderResult.java): Generic payload list wrapper carrying metadata (providerId, providerName, status, latencyMs, `List<T> data`, errorMessage).
    * [FlightOffer](file:///Users/prathamshah/Desktop/Work/Personal/ScatterMerge/src/main/java/com/prathamcodes/ScatterMerge/model/dto/FlightOffer.java): Example domain DTO (flightNumber, price, currency, duration).
    * [AggregationResponse<T>](file:///Users/prathamshah/Desktop/Work/Personal/ScatterMerge/src/main/java/com/prathamcodes/ScatterMerge/model/dto/AggregationResponse.java): Generic unified payload with aggregated lists and timing statistics.
    * [JobAcceptedResponse](file:///Users/prathamshah/Desktop/Work/Personal/ScatterMerge/src/main/java/com/prathamcodes/ScatterMerge/model/dto/JobAcceptedResponse.java): Returned immediately to asynchronous HTTP callers.
  * Define enums:
    * `JobStatus`: `SUBMITTED`, `PROCESSING`, `COMPLETED`, `FAILED`.
    * `ProviderStatus`: `SUCCESS`, `TIMEOUT`, `FAILED_FALLBACK`.
* **Daily Achievement**: Compilable generic data records and status enums mapping the system lifecycle.

---

### Day 2: Database Persistence & Repository Layer
* **Goal**: Establish database schemas to track, audit, and log aggregate jobs.
* **Tasks**:
  * Define the JPA entity `AggregationJob` under `model/entity/` containing:
    * `jobId` (UUID string), `status` (JobStatus enum), `origin`, `destination`, `webhookUrl`, raw serialized results, timestamps (`createdAt`, `updatedAt`).
  * Create `repository/AggregationJobRepository.java` extending `JpaRepository`.
  * Enable JPA Auditing in [JpaConfig.java](file:///Users/prathamshah/Desktop/Work/Personal/ScatterMerge/src/main/java/com/prathamcodes/ScatterMerge/config/JpaConfig.java) to auto-generate timestamps.
* **Daily Achievement**: Database tables auto-generated in PostgreSQL on startup. Repository interface tested to successfully save and read aggregation jobs.

---

### Day 3: WireMock External API Simulators
* **Goal**: Setup external sandbox endpoints simulating slow responses and errors.
* **Tasks**:
  * Set up WireMock configuration inside [WireMockConfig.java](file:///Users/prathamshah/Desktop/Work/Personal/ScatterMerge/src/main/java/com/prathamcodes/ScatterMerge/config/WireMockConfig.java).
  * Configure simulated endpoints for 5 mock airlines:
    1. **SkyHigh Airways**: 150ms delay, returns 2 flight offers.
    2. **Oceanic Air**: 320ms delay, returns 3 flight offers.
    3. **TransGlobal Jets**: 2500ms delay (designed to trigger timeout fallback).
    4. **StarFlight**: 450ms delay, returns 1 flight offer.
    5. **Falcon Express**: 100ms delay, returns an HTTP `500 Internal Server Error`.
* **Daily Achievement**: A running mock environment providing controlled latencies and failure modes.

---

## Phase 2: Concurrent Connector Layer (Days 4–8)

### Day 4: High-Concurrency HTTP Client Config
* **Goal**: Configure an underlying HTTP engine resilient against thread starvation under load.
* **Tasks**:
  * Configure [HttpClientConfig.java](file:///Users/prathamshah/Desktop/Work/Personal/ScatterMerge/src/main/java/com/prathamcodes/ScatterMerge/config/HttpClientConfig.java).
  * Initialize an instance of Java's new `HttpClient` or Spring's `RestClient`.
  * Configure connection pool timeouts, socket timeouts (e.g. 5 seconds), and keep-alive rules.
* **Daily Achievement**: An optimized HTTP client bean ready for dependency injection.

---

### Day 5: Generic Provider Connector & Domain Adapter
* **Goal**: Implement generic provider abstraction layer capable of wrapping different payload schemas.
* **Tasks**:
  * Define generic interface [ProviderConnector<T>](file:///Users/prathamshah/Desktop/Work/Personal/ScatterMerge/src/main/java/com/prathamcodes/ScatterMerge/service/provider/ProviderConnector.java) with `getProviderId()`, `getProviderName()`, and generic output type `ProviderResult<T> fetchOffers(...)`.
  * Implement [AirlineProviderClient](file:///Users/prathamshah/Desktop/Work/Personal/ScatterMerge/src/main/java/com/prathamcodes/ScatterMerge/service/provider/AirlineProviderClient.java) implementing `ProviderConnector<FlightOffer>`. Map the third-party's deep JSON response structure directly to `ProviderResult<FlightOffer>` inside this client adapter.
* **Daily Achievement**: Strongly-typed domain adapter mapping specialized airline APIs to the generic interface.

---

### Day 6: Generic Fallback Provider Factory
* **Goal**: Build generic client fallbacks that run instantly when a partner is offline or slow.
* **Tasks**:
  * Create [FallbackProviderFactory.java](file:///Users/prathamshah/Desktop/Work/Personal/ScatterMerge/src/main/java/com/prathamcodes/ScatterMerge/service/provider/FallbackProviderFactory.java) capable of generating default mock results of type `T`.
  * Write fallback generators returning empty lists or static default configurations, logging the exact exception reason (`TIMEOUT` vs `HTTP_ERROR`).
* **Daily Achievement**: Resilient generic factory class producing domain fallbacks on demand.

---

### Day 7: Generic Scatter-Gather Concurrency Core
* **Goal**: Fan-out outgoing requests concurrently using Project Loom Virtual Threads.
* **Tasks**:
  * Build the layout of [ScatterGatherEngine.java](file:///Users/prathamshah/Desktop/Work/Personal/ScatterMerge/src/main/java/com/prathamcodes/ScatterMerge/service/ScatterGatherEngine.java) using class parameter `<T>`.
  * Configure the executor using `Executors.newVirtualThreadPerTaskExecutor()`.
  * Launch concurrent generic tasks utilizing `CompletableFuture.supplyAsync(..., virtualThreadExecutor)` for each registered `ProviderConnector<T>`.
* **Daily Achievement**: Thread-efficient parallel execution of generic tasks on virtual threads.

---

### Day 8: Timeout Truncation & Exception Catching
* **Goal**: Impose strict latency ceilings on parallel outbound calls.
* **Tasks**:
  * Complete [ScatterGatherEngine.java](file:///Users/prathamshah/Desktop/Work/Personal/ScatterMerge/src/main/java/com/prathamcodes/ScatterMerge/service/ScatterGatherEngine.java).
  * Attach `.orTimeout(2, TimeUnit.SECONDS)` to enforce a hard SLA threshold.
  * Connect `.exceptionally()` to intercept exceptions/timeouts and swap in the Fallback Factory results.
  * Combine elements using `CompletableFuture.allOf().join()` to block orchestrator until all generic threads complete.
* **Daily Achievement**: Complete execution barrier restricting total API processing time to ~2 seconds, yielding standard fallbacks on timeouts or HTTP 500s.

---

## Phase 3: Aggregation & Delivery (Days 9–14)

### Day 9: Generic Result Aggregator & Stats Engine
* **Goal**: Standardize and sort data received from various generic providers.
* **Tasks**:
  * Build generic [ResultAggregator.java](file:///Users/prathamshah/Desktop/Work/Personal/ScatterMerge/src/main/java/com/prathamcodes/ScatterMerge/service/ResultAggregator.java) using `<T>`.
  * Define generic sorting policies (e.g. implementing custom comparator logic or sorting DTO elements if they implement Comparable).
  * Compute analytics: total duration, successful queries, and fallback/timeout counts.
* **Daily Achievement**: Returns a single parameterized `AggregationResponse<T>` sorting elements from successful providers.

---

### Day 10: HMAC Security Signatures
* **Goal**: Support secure verification of delivery payload authenticity.
* **Tasks**:
  * Implement [HmacSecurityUtil.java](file:///Users/prathamshah/Desktop/Work/Personal/ScatterMerge/src/main/java/com/prathamcodes/ScatterMerge/util/HmacSecurityUtil.java).
  * Create helper methods computing HMAC-SHA256 digests using a shared secret key and the raw payload JSON.
* **Daily Achievement**: Passing unit tests verifying matching signatures are generated for identical payloads.

---

### Day 11: Webhook Dispatcher Foundation
* **Goal**: Dispatch aggregated payloads asynchronously in the background.
* **Tasks**:
  * Implement generic [WebhookDispatcherService.java](file:///Users/prathamshah/Desktop/Work/Personal/ScatterMerge/src/main/java/com/prathamcodes/ScatterMerge/service/WebhookDispatcherService.java).
  * Configure `@EnableAsync` with a custom TaskExecutor configured to use virtual threads.
  * Send basic POST requests carrying the calculated `X-ScatterMerge-Signature` header to target clients.
* **Daily Achievement**: Webhook engine successfully firing background POST events containing signatures and aggregate payloads.

---

### Day 12: Exponential Backoff Retry Policy
* **Goal**: Make webhook delivery resilient to transient client issues.
* **Tasks**:
  * Update generic [WebhookDispatcherService.java](file:///Users/prathamshah/Desktop/Work/Personal/ScatterMerge/src/main/java/com/prathamcodes/ScatterMerge/service/WebhookDispatcherService.java).
  * Build an active retry loop (up to 3 times) on non-2xx failures, implementing backoff multiplier steps (e.g. 1s, 2s, 4s).
  * Introduce mathematical jitter to avoid target backend thundering herds.
* **Daily Achievement**: Robust webhook retry mechanism handling transient connection errors gracefully.

---

### Day 13: Dead Letter Queue (DLQ) & Admin Manual Retry
* **Goal**: Audit failed webhooks and offer manual recovery hooks.
* **Tasks**:
  * Add transactional checks: if a webhook exhausts all retries, record it as a delivery failure state (`WEBHOOK_FAILED`).
  * Implement a REST controller [WebhookRetryController.java](file:///Users/prathamshah/Desktop/Work/Personal/ScatterMerge/src/main/java/com/prathamcodes/ScatterMerge/controller/WebhookRetryController.java) to allow admins to retry a job delivery manually via REST request.
* **Daily Achievement**: Failed webhooks audited in the database, with a REST endpoint available to trigger manual retries.

---

### Day 14: Job Orchestration Integration
* **Goal**: Wire the ingestion, database, concurrency engine, and webhooks together.
* **Tasks**:
  * Build generic [AggregationOrchestrator.java](file:///Users/prathamshah/Desktop/Work/Personal/ScatterMerge/src/main/java/com/prathamcodes/ScatterMerge/service/AggregationOrchestrator.java).
  * Implement flow: save job as `PROCESSING` -> run `ScatterGatherEngine<T>` -> run `ResultAggregator<T>` -> write final result back to the database -> trigger async `WebhookDispatcherService<T>` -> update job as `COMPLETED`.
* **Daily Achievement**: The entire processing and dispatch flow executed programmatically.

---

## Phase 4: API Presentation & Testing (Days 15–18)

### Day 15: Controller Endpoints & Validation
* **Goal**: Expose the endpoints to users.
* **Tasks**:
  * Complete [AggregationController.java](file:///Users/prathamshah/Desktop/Work/Personal/ScatterMerge/src/main/java/com/prathamcodes/ScatterMerge/controller/AggregationController.java) implementing `POST /api/v1/aggregate`.
  * Enforce input validations using `jakarta.validation` (`@NotBlank`, `@URL`, etc.).
  * Complete [JobStatusController.java](file:///Users/prathamshah/Desktop/Work/Personal/ScatterMerge/src/main/java/com/prathamcodes/ScatterMerge/controller/JobStatusController.java) mapping `GET /api/v1/jobs/{jobId}` for polling fallback.
* **Daily Achievement**: Exposing clean, validated APIs running on port 8081.

---

### Day 16: Controller Advice & Trace Logging
* **Goal**: Ensure structured API error formatting and diagnostic traceability.
* **Tasks**:
  * Implement a `@RestControllerAdvice` class converting validation errors into readable JSON structures.
  * Standardize log messaging with SLF4J and MDC to tag logs with the current `jobId`.
* **Daily Achievement**: Invalid client inputs return standard error shapes. Detailed log files trace jobs by ID.

---

### Day 17: Scatter-Gather Integration Tests
* **Goal**: Programmatically verify engine concurrency, timeouts, and fallback routing.
* **Tasks**:
  * Create `ScatterGatherEngineTest.java` utilizing JUnit 5, `@SpringBootTest`, and WireMock.
  * Verify that a slow response is safely intercepted and triggers a fallback, and that the total process does not stall.
* **Daily Achievement**: Automated suite validating parallel task aggregation under network variations.

---

### Day 18: Webhook Dispatcher Integration Tests
* **Goal**: Programmatically verify webhook signing and backoff retries.
* **Tasks**:
  * Create `WebhookDispatcherIntegrationTest.java` using JUnit 5.
  * Verify signature authenticity calculations match.
  * Verify retry scheduling and backoff steps against a mock server.
* **Daily Achievement**: Test coverage verifying delivery retries and HMAC headers.

---

## Phase 5: Production Readiness (Days 19–20)

### Day 19: Micrometer Metrics & Actuator
* **Goal**: Monitor the operational state of the service.
* **Tasks**:
  * Enable Spring Boot Actuator dependencies.
  * Register custom Micrometer indicators tracking key metrics:
    * `scattermerge.provider.latency`
    * `scattermerge.provider.timeout.count`
    * `scattermerge.webhook.retry.count`
* **Daily Achievement**: Actuator endpoints (`/actuator/metrics` and `/actuator/health`) exposing key performance and latency stats.

---

### Day 20: Documentation & End-to-End Live Walkthrough
* **Goal**: Write developer documentation and perform a manual demonstration run.
* **Tasks**:
  * Write a detailed setup manual including sample `curl` commands for Job Submission, Job Polling, and Webhook verification.
  * Verify the system end-to-end against local DB and simulated endpoints, checking logs and table updates.
* **Daily Achievement**: A fully documented, battle-tested backend engine ready for deployment.
