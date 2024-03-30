package edu.java.scrapper.domain.jdbc;

import edu.java.scrapper.IntegrationEnvironment;
import edu.java.scrapper.domain.entity.TgChat;
import edu.java.scrapper.domain.jbdc.JdbcTgChatRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
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

    @DynamicPropertySource
    public static void setJpaAccessType(DynamicPropertyRegistry registry) {
        registry.add("app.database-access-type", () -> "jdbc");
    }

    @Test
    @Transactional
    public void addTest() {
        Long expectedId = 1L;
        TgChat tgChat = new TgChat(expectedId, OffsetDateTime.now());
        TgChat savedTgChat = chatRepository.add(tgChat);

        assertNotNull(savedTgChat);
        assertEquals(expectedId, savedTgChat.getId());

        int count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM chat WHERE id = ?",
            new Object[] {expectedId},
            Integer.class
        );
        assertEquals(1, count);
    }

    @Test
    @Transactional
    public void removeTest() {
        long expectedId = 1;
        TgChat tgChat = new TgChat(expectedId, OffsetDateTime.now());
        chatRepository.add(tgChat);

        chatRepository.remove(tgChat);

        int count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM chat WHERE id = ?",
            new Object[] {expectedId},
            Integer.class
        );
        assertEquals(0, count);
    }
}
