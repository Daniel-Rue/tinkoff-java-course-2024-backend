package edu.java.scrapper.service.notification_sender;

import edu.java.model.dto.request.LinkUpdateRequest;
import edu.java.scrapper.client.BotClient;
import edu.java.scrapper.service.NotificationSenderService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RestNotificationSenderService implements NotificationSenderService {
    private final BotClient botClient;

    @Override
    public void sendUpdate(LinkUpdateRequest updateRequest)  {
        botClient.sendUpdate(updateRequest).block();
    }
}
