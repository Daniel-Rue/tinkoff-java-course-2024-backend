package edu.java.scrapper.client;

import edu.java.scrapper.configuration.client.StackOverflowConfig;
import edu.java.scrapper.dto.stackoverflow.StackOverFlowAnswersResponse;
import edu.java.scrapper.dto.stackoverflow.StackOverFlowQuestionLastActivityResponse;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

public class StackOverflowClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(StackOverflowClient.class);
    private final WebClient webClient;

    private final int maxRetryAttempts;
    private final long retryDelay;

    public StackOverflowClient(StackOverflowConfig stackOverflowConfig) {
        this.webClient = WebClient.builder().baseUrl(stackOverflowConfig.getBaseUrl()).build();
        this.maxRetryAttempts = stackOverflowConfig.getMaxRetryAttempts();
        this.retryDelay = stackOverflowConfig.getRetryDelay();
    }

    public Mono<StackOverFlowQuestionLastActivityResponse> fetchQuestionLastActivity(int questionId) {
        return webClient.get()
            .uri("/questions/{questionId}?site=stackoverflow", questionId)
            .retrieve()
            .bodyToMono(StackOverFlowQuestionLastActivityResponse.class)
            .retryWhen(retrySpec())
            .doOnError(error -> LOGGER.error("Error fetching question last activity: {}", error.getMessage()))
            .onErrorResume(e -> Mono.empty());
    }

    public Mono<StackOverFlowAnswersResponse> fetchNewAnswers(int questionId) {
        return webClient.get()
            .uri("/questions/{questionId}/answers?order=desc&sort=creation&site=stackoverflow", questionId)
            .retrieve()
            .bodyToMono(StackOverFlowAnswersResponse.class)
            .retryWhen(retrySpec())
            .doOnError(error -> LOGGER.error("Error fetching new answers: {}", error.getMessage()))
            .onErrorResume(e -> Mono.empty());
    }

    private Retry retrySpec() {
        return Retry.fixedDelay(maxRetryAttempts, Duration.ofMillis(retryDelay))
            .filter(throwable -> throwable instanceof WebClientResponseException
                                 && ((WebClientResponseException) throwable).getStatusCode().is5xxServerError());
    }
}
