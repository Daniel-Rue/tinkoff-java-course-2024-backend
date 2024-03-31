package edu.java.scrapper.configuration.client;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "client.bot")
public class BotConfig {
    private String baseUrl;
    private int maxRetryAttempts;
    private long retryDelay;
    private List<Integer> retryStatusCodes;
}
