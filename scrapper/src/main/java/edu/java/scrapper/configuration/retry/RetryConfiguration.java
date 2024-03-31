package edu.java.scrapper.configuration.retry;

import edu.java.scrapper.configuration.ApplicationConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
public class RetryConfiguration {

    private final ApplicationConfig applicationConfig;

    public RetryConfiguration(ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
    }

    @Bean
    public RetryTemplate constantRetryTemplate() {
        var config = applicationConfig.retry().delayConfig().constant();
        var retryTemplate = new RetryTemplate();

        var fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(config.backOffPeriodMillis());

        retryTemplate.setBackOffPolicy(fixedBackOffPolicy);

        return retryTemplate;
    }

    @Bean
    public RetryTemplate linearRetryTemplate() {
        var config = applicationConfig.retry().delayConfig().linear();
        var retryTemplate = new RetryTemplate();

        var linearBackOffPolicy = new LinearBackOffPolicy();
        linearBackOffPolicy.setInitialInterval(config.initialIntervalMillis());
        linearBackOffPolicy.setMaxInterval(config.maxIntervalMillis());

        retryTemplate.setBackOffPolicy(linearBackOffPolicy);

        return retryTemplate;
    }

    @Bean
    public RetryTemplate exponentialRetryTemplate() {
        var config = applicationConfig.retry().delayConfig().exponential();
        var retryTemplate = new RetryTemplate();

        var exponentialBackOffPolicy = new ExponentialBackOffPolicy();
        exponentialBackOffPolicy.setInitialInterval(config.initialIntervalMillis());
        exponentialBackOffPolicy.setMultiplier(config.multiplier());
        exponentialBackOffPolicy.setMaxInterval(config.maxIntervalMillis());

        retryTemplate.setBackOffPolicy(exponentialBackOffPolicy);

        return retryTemplate;
    }
}
