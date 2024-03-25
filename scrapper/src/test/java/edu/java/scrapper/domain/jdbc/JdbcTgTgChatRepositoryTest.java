package edu.java.scrapper.domain.jdbc;

import edu.java.scrapper.IntegrationEnvironment;
import edu.java.scrapper.domain.entity.TgChat;
import edu.java.scrapper.domain.jbdc.JdbcTgChatRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.time.OffsetDateTime;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@SpringBootTest
@Testcontainers
public class JdbcTgTgChatRepositoryTest extends IntegrationEnvironment {

    @Autowired
    private JdbcTgChatRepository chatRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @Transactional
    @Rollback
    public void addTest() {
        TgChat tgChat = new TgChat(null, OffsetDateTime.now());
        TgChat savedTgChat = chatRepository.add(tgChat);

        assertNotNull(savedTgChat.getId());

        int count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM chat WHERE id = ?",
            new Object[] {savedTgChat.getId()},
            Integer.class
        );
        assertEquals(1, count);
    }

    @Test
    @Transactional
    @Rollback
    public void removeTest() {
        TgChat tgChat = new TgChat(null, OffsetDateTime.now());
        TgChat savedTgChat = chatRepository.add(tgChat);

        chatRepository.remove(savedTgChat);

        int count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM chat WHERE id = ?",
            new Object[] {savedTgChat.getId()},
            Integer.class
        );
        assertEquals(0, count);
    }
}
