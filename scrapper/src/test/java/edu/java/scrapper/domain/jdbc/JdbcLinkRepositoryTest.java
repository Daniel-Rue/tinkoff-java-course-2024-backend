package edu.java.scrapper.domain.jdbc;

import edu.java.scrapper.domain.entity.Link;
import edu.java.scrapper.domain.jbdc.JdbcLinkRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Optional;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@SpringBootTest
@Testcontainers
public class JdbcLinkRepositoryTest {

    @Autowired
    private JdbcLinkRepository linkRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @DynamicPropertySource
    public static void setJpaAccessType(DynamicPropertyRegistry registry) {
        registry.add("app.database-access-type", () -> "jdbc");
    }

    private static long nextChatId = 1;

    private Long createChatAndReturnId() {
        Long chatId = nextChatId++;
        jdbcTemplate.update("INSERT INTO chat (id, created_at) VALUES (?, ?)", chatId, OffsetDateTime.now());
        return chatId;
    }

    @BeforeEach
    void setup() {
        jdbcTemplate.update("DELETE FROM chat_link");
        jdbcTemplate.update("DELETE FROM link");
        jdbcTemplate.update("DELETE FROM chat");
    }

    @Test
    @Transactional
    @Rollback
    void addAndFindLink() {
        Long chatId = createChatAndReturnId();
        URI url = URI.create("https://example.com");
        Link newLink = new Link(null, url.toString(), OffsetDateTime.now(), OffsetDateTime.now(), "TestCreator");
        Link savedLink = linkRepository.add(newLink, chatId);

        assertNotNull(savedLink.getId());
        Optional<Link> foundLink = linkRepository.findByUrlAndChatId(url, chatId);
        assertTrue(foundLink.isPresent());
        assertEquals(url.toString(), foundLink.get().getUrl());
    }

    @Test
    @Transactional
    @Rollback
    void removeLink() {
        Long chatId = createChatAndReturnId();
        URI url = URI.create("https://example-to-remove.com");
        Link newLink = new Link(null, url.toString(), OffsetDateTime.now(), OffsetDateTime.now(), "TestCreatorToRemove");
        Link savedLink = linkRepository.add(newLink, chatId);

        linkRepository.remove(savedLink.getId(), chatId);

        assertFalse(linkRepository.existsByUrlAndChatId(url, chatId));
    }
}
