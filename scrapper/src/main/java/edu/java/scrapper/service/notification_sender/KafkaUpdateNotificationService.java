package edu.java.scrapper.service.notification_sender;

import edu.java.model.dto.request.LinkUpdateRequest;
import edu.java.scrapper.configuration.ApplicationConfig;
import edu.java.scrapper.service.UpdateNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;

@RequiredArgsConstructor
public class KafkaUpdateNotificationService implements UpdateNotificationService {

    private final KafkaTemplate<Long, LinkUpdateRequest>kafkaTemplate;
    private final ApplicationConfig applicationConfig;

    @Override
    public void sendUpdate(LinkUpdateRequest update) {
        String topicName = applicationConfig.kafkaConfig().updatesTopic().name();
        kafkaTemplate.send(topicName, update);
    }
}
