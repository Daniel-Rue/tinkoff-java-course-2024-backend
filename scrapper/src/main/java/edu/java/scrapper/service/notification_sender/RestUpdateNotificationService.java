package edu.java.scrapper.service.notification_sender;

import edu.java.model.dto.request.LinkUpdateRequest;
import edu.java.scrapper.client.BotClient;
import edu.java.scrapper.service.UpdateNotificationService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RestUpdateNotificationService implements UpdateNotificationService {
    private final BotClient botClient;

    @Override
    public void sendUpdate(LinkUpdateRequest updateRequest) {
        botClient.sendUpdate(updateRequest).block();
    }
}
