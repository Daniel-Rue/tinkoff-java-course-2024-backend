package edu.java.scrapper.client;

import edu.java.scrapper.configuration.client.GitHubConfig;
import edu.java.scrapper.dto.github.GitHubCommitResponse;
import edu.java.scrapper.dto.github.GitHubLastUpdateResponse;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

public class GitHubClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(GitHubClient.class);
    private final WebClient webClient;

    private final int maxRetryAttempts;
    private final long retryDelay;
    private final Set<Integer> retryStatusCodes;

    public GitHubClient(GitHubConfig gitHubConfig) {
        this.webClient = WebClient.builder().baseUrl(gitHubConfig.getBaseUrl()).build();
        this.maxRetryAttempts = gitHubConfig.getMaxRetryAttempts();
        this.retryDelay = gitHubConfig.getRetryDelay();
        this.retryStatusCodes = new HashSet<>(gitHubConfig.getRetryStatusCodes());
    }

    public Mono<GitHubLastUpdateResponse> fetchRepoLastUpdated(String owner, String repo) {
        return webClient.get()
            .uri("/repos/{owner}/{repo}", owner, repo)
            .retrieve()
            .bodyToMono(GitHubLastUpdateResponse.class)
            .retryWhen(retrySpec())
            .doOnError(error -> LOGGER.error(
                "Error fetching the last update for repo: {}/{} - {}",
                owner,
                repo,
                error.getMessage()
            ))
            .onErrorResume(error -> Mono.empty());
    }

    public Mono<List<GitHubCommitResponse>> fetchCommitsSince(String owner, String repo, OffsetDateTime since) {
        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/repos/{owner}/{repo}/commits")
                .queryParam("since", since.toString())
                .build(owner, repo))
            .retrieve()
            .bodyToFlux(GitHubCommitResponse.class)
            .collectList()
            .retryWhen(retrySpec())
            .doOnSuccess(commits -> LOGGER.debug("Successfully fetched {} commits since {}.", commits.size(), since))
            .doOnError(error -> LOGGER.error("Error fetching commits since {}: {}", since, error.getMessage()));
    }

    private Retry retrySpec() {
        return Retry.fixedDelay(maxRetryAttempts, Duration.ofMillis(retryDelay))
            .filter(throwable -> throwable instanceof WebClientResponseException
                                 && retryStatusCodes.contains(((WebClientResponseException) throwable).getStatusCode()
                .value()));
    }
}
