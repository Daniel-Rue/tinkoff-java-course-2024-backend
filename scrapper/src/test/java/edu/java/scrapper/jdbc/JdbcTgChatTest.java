package edu.java.scrapper.jdbc;

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
public class JdbcTgChatTest extends IntegrationEnvironment {

    @Autowired
    private JdbcTgChatRepository chatRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @Transactional
    @Rollback
    public void addTest() {
        TgChat chat = new TgChat(null, OffsetDateTime.now());
        TgChat savedChat = chatRepository.add(chat);

        assertNotNull(savedChat.getId());

        int count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM chat WHERE id = ?",
            new Object[] {savedChat.getId()},
            Integer.class
        );
        assertEquals(1, count);
    }

    @Test
    @Transactional
    @Rollback
    public void removeTest() {
        TgChat chat = new TgChat(null, OffsetDateTime.now());
        TgChat savedChat = chatRepository.add(chat);

        chatRepository.remove(savedChat);

        int count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM chat WHERE id = ?",
            new Object[] {savedChat.getId()},
            Integer.class
        );
        assertEquals(0, count);
    }
}