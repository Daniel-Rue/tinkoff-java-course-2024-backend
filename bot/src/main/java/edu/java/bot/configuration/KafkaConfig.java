package edu.java.bot.configuration;

import edu.java.model.dto.request.LinkUpdateRequest;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
@EnableKafka
@RequiredArgsConstructor
public class KafkaConfig {
    private final ApplicationConfig config;

    @Bean
    public NewTopic topicLinkUpdatesDlq() {
        return TopicBuilder
            .name(config.kafkaConfig().updatesTopicDlq().name())
            .partitions(config.kafkaConfig().updatesTopicDlq().partitions())
            .replicas(config.kafkaConfig().updatesTopicDlq().replicas())
            .build();
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<Long, LinkUpdateRequest>
    linkUpdatesRequestConcurrentKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Long, LinkUpdateRequest> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(Map.of(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, config.kafkaConfig().bootstrapServers(),
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class.getName(),
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class.getName(),
            ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, LongDeserializer.class.getName(),
            ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class.getName(),
            JsonDeserializer.TRUSTED_PACKAGES, "*",
            JsonDeserializer.USE_TYPE_INFO_HEADERS, "false",
            JsonDeserializer.VALUE_DEFAULT_TYPE, LinkUpdateRequest.class.getName()
        )));

        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
            linkUpdatesDlqKafkaTemplate(linkUpdatesRequestDlqProducerFactory()),
            (r, e) -> new org.apache.kafka.common.TopicPartition(config.kafkaConfig().updatesTopicDlq().name(),
                r.partition()));

        factory.setCommonErrorHandler(new DefaultErrorHandler(recoverer, new FixedBackOff(0L, 0L)));

        return factory;
    }

    @Bean
    public ProducerFactory<Long, LinkUpdateRequest> linkUpdatesRequestDlqProducerFactory() {
        return new DefaultKafkaProducerFactory<>(Map.of(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.kafkaConfig().bootstrapServers(),
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class
        ));
    }

    @Bean
    public KafkaTemplate<Long, LinkUpdateRequest> linkUpdatesDlqKafkaTemplate(
        @Qualifier("linkUpdatesRequestDlqProducerFactory") ProducerFactory<Long, LinkUpdateRequest> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}
