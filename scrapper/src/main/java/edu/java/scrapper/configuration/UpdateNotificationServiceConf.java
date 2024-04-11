package edu.java.scrapper.configuration;

import edu.java.model.dto.request.LinkUpdateRequest;
import edu.java.scrapper.client.BotClient;
import edu.java.scrapper.service.NotificationSenderService;
import edu.java.scrapper.service.notification_sender.KafkaNotificationProducerService;
import edu.java.scrapper.service.notification_sender.RestNotificationSenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@RequiredArgsConstructor
public class UpdateNotificationServiceConf {
    private final ApplicationConfig config;
    private final KafkaTemplate<Long, LinkUpdateRequest> linkUpdateRequestKafkaTemplate;
    private final BotClient botClient;

    @Bean
    public NotificationSenderService updateNotificationService() {
        if (config.useQueue()) {
            return new KafkaNotificationProducerService(linkUpdateRequestKafkaTemplate, config);
        } else {
            return new RestNotificationSenderService(botClient);
        }
    }
}
