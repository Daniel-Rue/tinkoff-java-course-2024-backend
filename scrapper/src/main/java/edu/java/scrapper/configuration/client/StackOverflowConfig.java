package edu.java.scrapper.configuration.client;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "client.stackoverflow")
public class StackOverflowConfig {
    private String baseUrl = "https://api.stackexchange.com/2.2";
    private int maxRetryAttempts = 3;
    private long retryDelay = 1000;
    private List<Integer> retryStatusCodes;
}
