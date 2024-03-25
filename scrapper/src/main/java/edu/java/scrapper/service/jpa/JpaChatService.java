package edu.java.scrapper.service.jpa;

import edu.java.scrapper.domain.entity.Chat;
import edu.java.scrapper.domain.jpa.JpaChatRepository;
import edu.java.scrapper.exception.ChatNotFoundException;
import edu.java.scrapper.exception.DuplicateRegistrationException;
import edu.java.scrapper.service.TgChatService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import java.time.OffsetDateTime;
import java.util.Optional;

@Primary
@Service
public class JpaChatService implements TgChatService {

    private final JpaChatRepository tgChatRepository;

    @Autowired
    public JpaChatService(JpaChatRepository tgChatRepository) {
        this.tgChatRepository = tgChatRepository;
    }

    @Override
    @Transactional
    public void register(long tgChatId) {
        boolean exists = tgChatRepository.existsById(tgChatId);
        if (exists) {
            throw new DuplicateRegistrationException("Chat with ID " + tgChatId + " is already registered.");
        }

        Chat newChat = new Chat();
        newChat.setId(tgChatId);
        newChat.setCreatedAt(OffsetDateTime.now());
        tgChatRepository.save(newChat);
    }

    @Override
    @Transactional
    public void unregister(long tgChatId) {
        Optional<Chat> chatOptional = tgChatRepository.findById(tgChatId);
        Chat chat = chatOptional.orElseThrow(() -> new ChatNotFoundException(tgChatId));

        // Удаляем чат
        tgChatRepository.delete(chat);
    }
}
