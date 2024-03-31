package edu.java.scrapper.client;

import edu.java.model.dto.request.LinkUpdateRequest;
import edu.java.model.dto.response.ApiErrorResponse;
import edu.java.scrapper.configuration.client.BotConfig;
import edu.java.scrapper.exception.ApiErrorResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import java.time.Duration;

public class BotClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(BotClient.class);
    private final WebClient webClient;

    private final int maxRetryAttempts;
    private final long retryDelay;

    public BotClient(BotConfig botConfig) {
        this.webClient = WebClient.builder().baseUrl(botConfig.getBaseUrl()).build();
        this.maxRetryAttempts = botConfig.getMaxRetryAttempts();
        this.retryDelay = botConfig.getRetryDelay();
    }

    public Mono<Void> sendUpdate(LinkUpdateRequest linkUpdate) {
        return webClient.post()
            .uri("/updates")
            .body(Mono.just(linkUpdate), LinkUpdateRequest.class)
            .retrieve()
            .onStatus(
                status -> status.is4xxClientError() || status.is5xxServerError(),
                response -> response.bodyToMono(ApiErrorResponse.class)
                    .map(ApiErrorResponseException::new)
            )
            .bodyToMono(Void.class)
            .retryWhen(retrySpec())
            .doOnError(error -> LOGGER.error("Error sending update: {}", error.getMessage()))
            .onErrorResume(error -> Mono.empty());
    }

    private Retry retrySpec() {
        return Retry.fixedDelay(maxRetryAttempts, Duration.ofMillis(retryDelay))
            .filter(throwable -> throwable instanceof WebClientResponseException &&
                                 ((WebClientResponseException) throwable).getStatusCode().is5xxServerError());
    }
}
