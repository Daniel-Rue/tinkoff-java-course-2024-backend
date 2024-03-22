package edu.java.scrapper.service.jdbc;

import edu.java.scrapper.domain.entity.TgChat;
import edu.java.scrapper.domain.jbdc.JdbcTgChatRepository;
import edu.java.scrapper.exception.ChatNotFoundException;
import edu.java.scrapper.exception.DuplicateRegistrationException;
import edu.java.scrapper.service.TgChatService;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Primary
@Service
@RequiredArgsConstructor
public class JdbcTgChatService implements TgChatService {

    private final JdbcTgChatRepository tgChatRepository;

    @Override
    @Transactional
    public void register(long tgChatId) {
        if (tgChatRepository.existsById(tgChatId)) {
            throw new DuplicateRegistrationException("Chat with ID " + tgChatId + " is already registered.");
        }

        TgChat newChat = new TgChat(tgChatId, OffsetDateTime.now());
        tgChatRepository.add(newChat);
    }

    @Override
    @Transactional
    public void unregister(long tgChatId) {
        TgChat chat = tgChatRepository.findById(tgChatId)
            .orElseThrow(() -> new ChatNotFoundException(tgChatId));

        tgChatRepository.remove(chat);
    }
}
