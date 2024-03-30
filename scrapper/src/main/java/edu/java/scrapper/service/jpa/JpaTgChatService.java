package edu.java.scrapper.service.jpa;

import edu.java.scrapper.domain.entity.TgChat;
import edu.java.scrapper.domain.jpa.JpaTgChatRepository;
import edu.java.scrapper.exception.ChatNotFoundException;
import edu.java.scrapper.exception.DuplicateRegistrationException;
import edu.java.scrapper.service.TgChatService;
import jakarta.transaction.Transactional;
import java.time.OffsetDateTime;
import java.util.Optional;

public class JpaTgChatService implements TgChatService {

    private final JpaTgChatRepository tgChatRepository;

    public JpaTgChatService(JpaTgChatRepository tgChatRepository) {
        this.tgChatRepository = tgChatRepository;
    }

    @Override
    @Transactional
    public void register(long tgChatId) {
        boolean exists = tgChatRepository.existsById(tgChatId);
        if (exists) {
            throw new DuplicateRegistrationException("Chat with ID " + tgChatId + " is already registered.");
        }

        TgChat newTgChat = new TgChat();
        newTgChat.setId(tgChatId);
        newTgChat.setCreatedAt(OffsetDateTime.now());
        tgChatRepository.save(newTgChat);
    }

    @Override
    @Transactional
    public void unregister(long tgChatId) {
        Optional<TgChat> chatOptional = tgChatRepository.findById(tgChatId);
        TgChat tgChat = chatOptional.orElseThrow(() -> new ChatNotFoundException(tgChatId));

        // Удаляем чат
        tgChatRepository.delete(tgChat);
    }
}
