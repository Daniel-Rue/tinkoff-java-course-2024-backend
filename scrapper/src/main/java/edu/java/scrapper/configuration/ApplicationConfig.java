package edu.java.scrapper.configuration;

import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import kafka.server.KafkaConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ApplicationConfig(
    @NotNull Scheduler scheduler,
    Client client,
    @NotNull AccessType databaseAccessType,
    @NotNull Boolean useQueue,
    KafkaConfig kafkaConfig
) {
    public enum AccessType {
        JDBC, JPA, JOOQ
    }

    public record Scheduler(
        boolean enable,
        @NotNull Duration interval,
        @NotNull Duration forceCheckDelay
    ) {
    }

    public record Client(
        String gitHub,
        String stackOverflow,
        String bot
    ) {
    }

    public record KafkaConfig(
        String bootstrapServers,
        UpdatesTopic updatesTopic
    ) {
        public record UpdatesTopic(
            String name,
            Integer partitions,
            Integer replicas
        ) {
        }
    }
}
