package edu.java.scrapper.client;

import edu.java.scrapper.configuration.GitHubConfig;
import edu.java.scrapper.dto.github.GitHubCommitResponse;
import edu.java.scrapper.dto.github.GitHubLastUpdateResponse;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class GitHubClient {
    private final WebClient webClient;

    public GitHubClient(GitHubConfig gitHubConfig) {
        this.webClient = WebClient.builder().baseUrl(gitHubConfig.getBaseUrl()).build();
    }

    public Mono<GitHubLastUpdateResponse> fetchRepoLastUpdated(String owner, String repo) {
        return webClient.get()
            .uri("/repos/{owner}/{repo}", owner, repo)
            .retrieve()
            .bodyToMono(GitHubLastUpdateResponse.class)
            .onErrorResume(e -> Mono.empty());
    }

    public Mono<List<GitHubCommitResponse>> fetchCommitsSince(String owner, String repo, OffsetDateTime since) {
        String url = String.format("/repos/%s/%s/commits?since=%s", owner, repo, since.toString());
        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path(url)
                .build())
            .retrieve()
            .bodyToFlux(GitHubCommitResponse.class)
            .collectList();
    }
}
