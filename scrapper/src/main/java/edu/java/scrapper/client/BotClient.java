package edu.java.scrapper.client;

import edu.java.model.dto.request.LinkUpdateRequest;
import edu.java.model.dto.response.ApiErrorResponse;
import edu.java.scrapper.configuration.BotConfig;
import edu.java.scrapper.exception.ApiErrorResponseException;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class BotClient {
    private final WebClient webClient;

    public BotClient(BotConfig botConfig) {
        this.webClient = WebClient.builder().baseUrl(botConfig.getBaseUrl()).build();
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
            .bodyToMono(Void.class);
    }
}
