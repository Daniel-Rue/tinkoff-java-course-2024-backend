package edu.java.scrapper.jdbc;

import edu.java.scrapper.IntegrationEnvironment;
import edu.java.scrapper.domain.entity.Link;
import edu.java.scrapper.domain.jbdc.JdbcLinkRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
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
public class JdbcLinkTest extends IntegrationEnvironment {

    @Autowired
    private JdbcLinkRepository linkRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Long chatId;

    @BeforeEach
    void setup() {
        chatId = jdbcTemplate.queryForObject("INSERT INTO chat (created_at) VALUES (?) RETURNING id",
            new Object[]{OffsetDateTime.now()},
            Long.class);
    }

    @AfterEach
    void cleanup() {
        jdbcTemplate.update("DELETE FROM chat_link");
        jdbcTemplate.update("DELETE FROM link");
        jdbcTemplate.update("DELETE FROM chat");
    }

    @Test
    void addAndFindLink() {
        URI url = URI.create("https://example.com");
        Link newLink = new Link(null, url.toString(), OffsetDateTime.now(), OffsetDateTime.now(), "TestCreator");
        Link savedLink = linkRepository.add(newLink, chatId);

        assertNotNull(savedLink.getId());
        Optional<Link> foundLink = linkRepository.findByUrlAndChatId(url, chatId);
        assertTrue(foundLink.isPresent());
        assertEquals(url.toString(), foundLink.get().getUrl());
    }

    @Test
    void removeLink() {
        URI url = URI.create("https://example-to-remove.com");
        Link newLink = new Link(null, url.toString(), OffsetDateTime.now(), OffsetDateTime.now(), "TestCreator");
        Link savedLink = linkRepository.add(newLink, chatId);

        linkRepository.remove(savedLink.getId(), chatId);

        assertFalse(linkRepository.existsByUrlAndChatId(url, chatId));
    }
}
