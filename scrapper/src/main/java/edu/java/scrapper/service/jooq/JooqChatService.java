package edu.java.scrapper.service.jooq;

import edu.java.scrapper.domain.jooq.JooqTgChatRepository;
import edu.java.scrapper.exception.ChatNotFoundException;
import edu.java.scrapper.exception.DuplicateRegistrationException;
import edu.java.scrapper.service.TgChatService;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JooqChatService implements TgChatService {

    private final JooqTgChatRepository tgChatRepository;

    @Override
    @Transactional
    public void register(long tgChatId) {
        if (tgChatRepository.existsById(tgChatId)) {
            throw new DuplicateRegistrationException("Chat with ID " + tgChatId + " is already registered.");
        }

        tgChatRepository.add(tgChatId, OffsetDateTime.now());
    }

    @Override
    @Transactional
    public void unregister(long tgChatId) {
        if (!tgChatRepository.existsById(tgChatId)) {
            throw new ChatNotFoundException(tgChatId);
        }

        tgChatRepository.remove(tgChatId);
    }
}
