package edu.java.bot.configuration.client;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "client.scrapper")
public class ScrapperConfig {
    private String baseUrl = "http://localhost:8080";
    private int maxRetryAttempts;
    private long retryDelay;
    private List<Integer> retryStatusCodes;
}
