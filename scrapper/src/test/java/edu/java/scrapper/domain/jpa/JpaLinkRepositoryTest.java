package edu.java.scrapper.domain.jpa;

import edu.java.scrapper.IntegrationEnvironment;
import edu.java.scrapper.domain.entity.Link;
import edu.java.scrapper.domain.entity.TgChat;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class JpaLinkRepositoryTest extends IntegrationEnvironment {

    @Autowired
    private JpaLinkRepository linkRepository;

    @DynamicPropertySource
    public static void setJpaAccessType(DynamicPropertyRegistry registry) {
        registry.add("app.database-access-type", () -> "jpa");
    }

    @Test
    @Transactional
    void whenFindByUrl_thenReturnsLink() {
        OffsetDateTime now = OffsetDateTime.now();
        TgChat chat = new TgChat(1L, now);
        Link link = new Link(null, "http://example.com", now, now, "user1");
        chat.getLinks().add(link);
        link.getTgChats().add(chat);

        linkRepository.save(link);

        Optional<Link> foundLink = linkRepository.findByUrl("http://example.com");

        assertTrue(foundLink.isPresent());
        assertEquals("http://example.com", foundLink.get().getUrl());
    }

    @Test
    @Transactional
    void whenFindLinksToCheck_thenReturnsLinks() {
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime thresholdTime = now.minusDays(1);
        Link linkToCheck = new Link(null, "http://checkme.com", thresholdTime.minusHours(1), now, "user3");

        linkRepository.save(linkToCheck);

        List<Link> linksToCheck = linkRepository.findLinksToCheck(thresholdTime);
        assertFalse(linksToCheck.isEmpty());
        assertEquals("http://checkme.com", linksToCheck.get(0).getUrl());
    }

}
