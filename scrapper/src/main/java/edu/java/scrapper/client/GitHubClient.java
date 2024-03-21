package edu.java.scrapper.client;

import edu.java.model.dto.response.ApiErrorResponse;
import edu.java.scrapper.configuration.GitHubConfig;
import edu.java.scrapper.dto.github.GitHubCommitResponse;
import edu.java.scrapper.dto.github.GitHubLastUpdateResponse;
import java.time.OffsetDateTime;
import java.util.List;
import edu.java.scrapper.exception.ApiErrorResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class GitHubClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(GitHubClient.class);
    private final WebClient webClient;

    public GitHubClient(GitHubConfig gitHubConfig) {
        this.webClient = WebClient.builder().baseUrl(gitHubConfig.getBaseUrl()).build();
    }

    public Mono<GitHubLastUpdateResponse> fetchRepoLastUpdated(String owner, String repo) {
        return webClient.get()
            .uri("/repos/{owner}/{repo}", owner, repo)
            .retrieve()
            .bodyToMono(GitHubLastUpdateResponse.class)
            .doOnError(error -> LOGGER.error("Error fetching the last update for repo: {}/{} - {}", owner, repo, error.getMessage()))
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
            .doOnSuccess(commits -> LOGGER.debug("Successfully fetched {} commits.", commits.size()))
            .doOnError(error -> LOGGER.error("Error fetching commits: {}", error.getMessage()));
    }

}
