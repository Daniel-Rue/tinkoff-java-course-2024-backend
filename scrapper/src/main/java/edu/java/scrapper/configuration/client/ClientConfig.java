package edu.java.scrapper.configuration.client;

import edu.java.scrapper.client.BotClient;
import edu.java.scrapper.client.GitHubClient;
import edu.java.scrapper.client.StackOverflowClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ClientConfig {

    private final GitHubConfig gitHubConfig;
    private final StackOverflowConfig stackOverFlowConfig;
    private final BotConfig botConfig;

    @Bean
    public GitHubClient gitHubClient() {
        return new GitHubClient(gitHubConfig);
    }

    @Bean
    public StackOverflowClient stackOverflowClient() {
        return new StackOverflowClient(stackOverFlowConfig);
    }

    @Bean
    public BotClient botClient() {
        return new BotClient(botConfig);
    }
}
