package edu.java.bot.client;

import edu.java.bot.configuration.client.ScrapperConfig;
import edu.java.bot.exception.ApiErrorResponseException;
import edu.java.model.dto.request.AddLinkRequest;
import edu.java.model.dto.request.RemoveLinkRequest;
import edu.java.model.dto.response.ApiErrorResponse;
import edu.java.model.dto.response.LinkResponse;
import edu.java.model.dto.response.ListLinksResponse;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

public class ScrapperClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScrapperClient.class);
    private static final String TG_CHAT_ID_HEADER_NAME = "Tg-Chat-Id";
    private static final String TG_CHAT_ID_URI = "/tg-chat/{id}";
    private static final String LINK_CONTROLLER_URI = "/links";

    private final WebClient webClient;
    private final int maxRetryAttempts;
    private final long retryDelay;
    private final Set<Integer> retryStatusCodes;

    public ScrapperClient(ScrapperConfig scrapperConfig) {
        this.webClient = WebClient.builder().baseUrl(scrapperConfig.getBaseUrl()).build();
        this.maxRetryAttempts = scrapperConfig.getMaxRetryAttempts();
        this.retryDelay = scrapperConfig.getRetryDelay();
        this.retryStatusCodes = new HashSet<>(scrapperConfig.getRetryStatusCodes());
    }

    private Retry retrySpec() {
        return Retry.fixedDelay(maxRetryAttempts, Duration.ofMillis(retryDelay))
            .filter(throwable -> throwable instanceof WebClientResponseException
                                 && retryStatusCodes.contains(((WebClientResponseException) throwable).getStatusCode()
                                     .value()));
    }

    public Mono<Void> registerChat(Long id) {
        return webClient.post()
            .uri(TG_CHAT_ID_URI, id)
            .retrieve()
            .onStatus(
                httpStatus -> httpStatus.is4xxClientError() || httpStatus.is5xxServerError(),
                response -> response.bodyToMono(ApiErrorResponse.class)
                    .flatMap(errorResponse -> Mono.error(new ApiErrorResponseException(errorResponse)))
            )
            .bodyToMono(Void.class)
            .retryWhen(retrySpec())
            .doOnError(error -> LOGGER.error("Error registering chat: {}", error.getMessage()))
            .onErrorResume(e -> Mono.empty());
    }

    public Mono<Void> deleteChat(Long id) {
        return webClient.delete()
            .uri(TG_CHAT_ID_URI, id)
            .retrieve()
            .onStatus(
                httpStatus -> httpStatus.is4xxClientError() || httpStatus.is5xxServerError(),
                response -> response.bodyToMono(ApiErrorResponse.class)
                    .flatMap(errorResponse -> Mono.error(new ApiErrorResponseException(errorResponse)))
            )
            .bodyToMono(Void.class)
            .retryWhen(retrySpec())
            .doOnError(error -> LOGGER.error("Error deleting chat: {}", error.getMessage()))
            .onErrorResume(e -> Mono.empty());
    }

    public Mono<ListLinksResponse> getAllLinks(Long tgChatId) {
        return webClient.get()
            .uri(LINK_CONTROLLER_URI)
            .header(TG_CHAT_ID_HEADER_NAME, Long.toString(tgChatId))
            .retrieve()
            .onStatus(
                httpStatus -> httpStatus.is4xxClientError() || httpStatus.is5xxServerError(),
                response -> response.bodyToMono(ApiErrorResponse.class)
                    .flatMap(errorResponse -> Mono.error(new ApiErrorResponseException(errorResponse)))
            )
            .bodyToMono(ListLinksResponse.class)
            .retryWhen(retrySpec())
            .doOnError(error -> LOGGER.error("Error fetching all links: {}", error.getMessage()))
            .onErrorResume(e -> Mono.empty());
    }

    public Mono<LinkResponse> addLink(Long tgChatId, AddLinkRequest request) {
        return webClient.post()
            .uri(LINK_CONTROLLER_URI)
            .header(TG_CHAT_ID_HEADER_NAME, Long.toString(tgChatId))
            .bodyValue(request)
            .retrieve()
            .onStatus(
                httpStatus -> httpStatus.is4xxClientError() || httpStatus.is5xxServerError(),
                response -> response.bodyToMono(ApiErrorResponse.class)
                    .flatMap(errorResponse -> Mono.error(new ApiErrorResponseException(errorResponse)))
            )
            .bodyToMono(LinkResponse.class)
            .retryWhen(retrySpec())
            .doOnError(error -> LOGGER.error("Error adding link: {}", error.getMessage()))
            .onErrorResume(e -> Mono.empty());
    }

    public Mono<Void> removeLink(Long tgChatId, RemoveLinkRequest request) {
        return webClient.method(HttpMethod.DELETE)
            .uri(LINK_CONTROLLER_URI)
            .header(TG_CHAT_ID_HEADER_NAME, Long.toString(tgChatId))
            .bodyValue(request)
            .retrieve()
            .onStatus(
                httpStatus -> httpStatus.is4xxClientError() || httpStatus.is5xxServerError(),
                response -> response.bodyToMono(ApiErrorResponse.class)
                    .flatMap(errorResponse -> Mono.error(new ApiErrorResponseException(errorResponse)))
            )
            .bodyToMono(Void.class)
            .retryWhen(retrySpec())
            .doOnError(error -> LOGGER.error("Error removing link: {}", error.getMessage()))
            .onErrorResume(e -> Mono.empty());
    }
}
