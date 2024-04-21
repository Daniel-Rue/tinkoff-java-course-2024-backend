package edu.java.bot.service.kafka;

import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.service.UpdateService;
import edu.java.model.dto.request.LinkUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class KafkaNotificationConsumerService {

    private final UpdateService updateService;
    private final KafkaTemplate<Long, LinkUpdateRequest> linkUpdatesDlqKafkaTemplate;
    private final ApplicationConfig config;


    @KafkaListener(groupId = "listeners.link.update",
                   topics = "${app.kafka-config.updates-topic.name}",
                   containerFactory = "linkUpdatesRequestConcurrentKafkaListenerContainerFactory",
                   concurrency = "2")
    public void listenLinkUpdateMessages(@Payload LinkUpdateRequest linkUpdateRequest) {
        log.info("Received new link update from scrapper: {} {}",
            linkUpdateRequest.description(),
            linkUpdateRequest.url());

        try {
            ResponseEntity<?> response = updateService.sendUpdate(linkUpdateRequest);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Failed to process update for link: " + linkUpdateRequest.url());
            }
        } catch (Exception e) {
            log.error("Error processing message from Kafka, moving to DLQ: {}", e.getMessage(), e);
            linkUpdatesDlqKafkaTemplate.send(
                config.kafkaConfig().updatesTopicDlq().name(),
                linkUpdateRequest.id(),
                linkUpdateRequest
            );
        }
    }
}
