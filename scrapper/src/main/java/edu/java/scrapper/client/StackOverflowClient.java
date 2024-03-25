package edu.java.scrapper.client;

import edu.java.scrapper.configuration.client.StackOverflowConfig;
import edu.java.scrapper.dto.stackoverflow.StackOverFlowAnswersResponse;
import edu.java.scrapper.dto.stackoverflow.StackOverFlowQuestionLastActivityResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class StackOverflowClient {
    private final WebClient webClient;

    public StackOverflowClient(StackOverflowConfig stackOverflowConfig) {
        this.webClient = WebClient.builder().baseUrl(stackOverflowConfig.getBaseUrl()).build();
    }

    public Mono<StackOverFlowQuestionLastActivityResponse> fetchQuestionLastActivity(int questionId) {
        return webClient.get()
            .uri("/questions/{questionId}?site=stackoverflow", questionId)
            .retrieve()
            .bodyToMono(StackOverFlowQuestionLastActivityResponse.class)
            .onErrorResume(e -> Mono.empty());
    }

    public Mono<StackOverFlowAnswersResponse> fetchNewAnswers(int questionId) {
        return webClient.get()
            .uri("/questions/{questionId}/answers?order=desc&sort=creation&site=stackoverflow", questionId)
            .retrieve()
            .bodyToMono(StackOverFlowAnswersResponse.class)
            .onErrorResume(e -> Mono.empty());
    }
}
