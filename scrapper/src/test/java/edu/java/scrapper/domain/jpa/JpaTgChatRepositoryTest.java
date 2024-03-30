package edu.java.scrapper.domain.jpa;

import edu.java.scrapper.domain.entity.TgChat;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import java.time.OffsetDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class JpaTgChatRepositoryTest {

    @Autowired
    private JpaTgChatRepository tgChatRepository;

    @DynamicPropertySource
    public static void setJpaAccessType(DynamicPropertyRegistry registry) {
        registry.add("app.database-access-type", () -> "jpa");
    }

    @Test
    @Transactional
    @Rollback
    public void whenSaveTgChat_thenFindById() {
        OffsetDateTime now = OffsetDateTime.now();
        TgChat chat = new TgChat(1L, now);

        TgChat savedChat = tgChatRepository.save(chat);
        TgChat foundChat = tgChatRepository.findById(savedChat.getId()).orElse(null);

        assertNotNull(foundChat);
        assertEquals(savedChat.getId(), foundChat.getId());
    }

    @Test
    @Transactional
    @Rollback
    public void whenDeleteTgChat_thenNotFound() {
        OffsetDateTime now = OffsetDateTime.now();
        TgChat chat = new TgChat(2L, now);

        TgChat savedChat = tgChatRepository.save(chat);
        tgChatRepository.deleteById(savedChat.getId());

        Optional<TgChat> deletedChat = tgChatRepository.findById(savedChat.getId());
        assertFalse(deletedChat.isPresent());
    }
}
