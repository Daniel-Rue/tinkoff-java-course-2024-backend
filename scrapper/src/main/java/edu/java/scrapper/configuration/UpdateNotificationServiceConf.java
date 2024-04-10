package edu.java.scrapper.configuration;

import edu.java.model.dto.request.LinkUpdateRequest;
import edu.java.scrapper.client.BotClient;
import edu.java.scrapper.service.UpdateNotificationService;
import edu.java.scrapper.service.notification_sender.KafkaUpdateNotificationService;
import edu.java.scrapper.service.notification_sender.RestUpdateNotificationService;
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
    public UpdateNotificationService updateNotificationService() {
        if (config.useQueue()) {
            return new KafkaUpdateNotificationService(linkUpdateRequestKafkaTemplate, config);
        } else {
            return new RestUpdateNotificationService(botClient);
        }
    }
}
