package edu.java.scrapper.client;

import edu.java.scrapper.configuration.GitHubConfig;
import edu.java.scrapper.dto.github.GitHubRepoResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class GitHubClient {
    private final WebClient webClient;

    public GitHubClient(GitHubConfig gitHubConfig) {
        this.webClient = WebClient.builder().baseUrl(gitHubConfig.getBaseUrl()).build();
    }

    public Mono<GitHubRepoResponse> fetchRepoInfo(String owner, String repo) {
        return this.webClient.get()
            .uri("/repos/{owner}/{repo}", owner, repo)
            .retrieve()
            .bodyToMono(GitHubRepoResponse.class);
    }
}
