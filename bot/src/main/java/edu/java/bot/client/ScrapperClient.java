package edu.java.bot.client;

import edu.java.bot.configuration.ScrapperConfig;
import edu.java.bot.exception.ApiErrorResponseException;
import edu.java.model.dto.request.AddLinkRequest;
import edu.java.model.dto.request.RemoveLinkRequest;
import edu.java.model.dto.response.ApiErrorResponse;
import edu.java.model.dto.response.LinkResponse;
import edu.java.model.dto.response.ListLinksResponse;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class ScrapperClient {
    private static final String TG_CHAT_ID_HEADER_NAME = "Tg-Chat-Id";
    private static final String TG_CHAT_ID_URI = "/tg-chat/{id}";
    private static final String LINK_CONTROLLER_URI = "/links";

    private final WebClient webClient;

    public ScrapperClient(ScrapperConfig botConfig) {
        this.webClient = WebClient.builder().baseUrl(botConfig.getBaseUrl()).build();
    }

    public Mono<Void> registerChat(Long id) {
        return webClient.post()
            .uri(TG_CHAT_ID_URI, id)
            .retrieve()
            .onStatus(httpStatus -> httpStatus.is4xxClientError() || httpStatus.is5xxServerError(), response ->
                response.bodyToMono(ApiErrorResponse.class)
                    .flatMap(error -> Mono.error(new ApiErrorResponseException(error))))
            .bodyToMono(Void.class);
    }

    public Mono<Void> deleteChat(Long id) {
        return webClient.delete()
            .uri(TG_CHAT_ID_URI, id)
            .retrieve()
            .onStatus(httpStatus -> httpStatus.is4xxClientError() || httpStatus.is5xxServerError(), response ->
                response.bodyToMono(ApiErrorResponse.class)
                    .flatMap(error -> Mono.error(new ApiErrorResponseException(error))))
            .bodyToMono(Void.class);
    }

    public Mono<ListLinksResponse> getAllLinks(Long tgChatId) {
        return webClient.get()
            .uri(LINK_CONTROLLER_URI)
            .header(TG_CHAT_ID_HEADER_NAME, Long.toString(tgChatId))
            .retrieve()
            .onStatus(httpStatus -> httpStatus.is4xxClientError() || httpStatus.is5xxServerError(), response ->
                response.bodyToMono(ApiErrorResponse.class)
                    .flatMap(error -> Mono.error(new ApiErrorResponseException(error))))
            .bodyToMono(ListLinksResponse.class);
    }

    public Mono<LinkResponse> addLink(Long tgChatId, AddLinkRequest request) {
        return webClient.post()
            .uri(LINK_CONTROLLER_URI)
            .header(TG_CHAT_ID_HEADER_NAME, Long.toString(tgChatId))
            .bodyValue(request)
            .retrieve()
            .onStatus(httpStatus -> httpStatus.is4xxClientError() || httpStatus.is5xxServerError(), response ->
                response.bodyToMono(ApiErrorResponse.class)
                    .flatMap(error -> Mono.error(new ApiErrorResponseException(error))))
            .bodyToMono(LinkResponse.class);
    }

    public Mono<Void> removeLink(Long tgChatId, RemoveLinkRequest request) {
        return webClient.method(HttpMethod.DELETE)
            .uri(LINK_CONTROLLER_URI)
            .header(TG_CHAT_ID_HEADER_NAME, Long.toString(tgChatId))
            .bodyValue(request)
            .retrieve()
            .onStatus(httpStatus -> httpStatus.is4xxClientError() || httpStatus.is5xxServerError(), response ->
                response.bodyToMono(ApiErrorResponse.class)
                    .map(ApiErrorResponseException::new))
            .bodyToMono(Void.class);
    }
}
