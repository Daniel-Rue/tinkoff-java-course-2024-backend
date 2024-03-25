package edu.java.bot.service;

import edu.java.model.dto.request.LinkUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UpdateService {

    private final BotService botService;

    @Autowired
    public UpdateService(BotService botService) {
        this.botService = botService;
    }

    public ResponseEntity<?> sendUpdate(LinkUpdateRequest updateRequest) {
        updateRequest.tgChatIds().forEach(chatId -> {
            String message =
                String.format("Update for link [%s]: %s", updateRequest.url(), updateRequest.description());
            botService.sendMessage(chatId, message);
        });
        return ResponseEntity.ok().build();
    }
}
