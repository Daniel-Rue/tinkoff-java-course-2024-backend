package edu.java.bot.service;

import edu.java.model.dto.request.LinkUpdateRequest;
import io.micrometer.core.instrument.Counter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateService {

    private final BotService botService;
    private final Counter processedMessagesCounter;

    public ResponseEntity<?> sendUpdate(LinkUpdateRequest updateRequest) {
        updateRequest.tgChatIds().forEach(chatId -> {
            String message =
                String.format("Update for link [%s]: %s", updateRequest.url(), updateRequest.description());
            botService.sendMessage(chatId, message);
        });
        processedMessagesCounter.increment();
        return ResponseEntity.ok().build();
    }
}
