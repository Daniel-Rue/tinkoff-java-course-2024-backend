//package edu.java.scrapper.domain.jooq;
//
//import edu.java.scrapper.domain.entity.Link;
//import org.jooq.DSLContext;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//import org.testcontainers.junit.jupiter.Testcontainers;
//import java.time.OffsetDateTime;
//import java.util.Optional;
//import static edu.java.scrapper.domain.jooq.codegen.tables.Chat.CHAT;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//@SpringBootTest
//@Testcontainers
//public class JooqLinkRepositoryTest {
//
//    @Autowired
//    private JooqLinkRepository linkRepository;
//
//    @Autowired
//    private DSLContext dsl;
//
//    private Link createTestLink(String url) {
//        OffsetDateTime now = OffsetDateTime.now();
//        return new Link(null, url, now, now, "testCreator");
//    }
//
//    private Long createTestChat() {
//        OffsetDateTime createdAt = OffsetDateTime.now();
//        return dsl.insertInto(CHAT, CHAT.CREATED_AT)
//            .values(createdAt)
//            .returning(CHAT.ID)
//            . fetchOne()
//            .getId();
//    }
//
//    @Test
//    @Transactional
//    public void addAndFindByIdTest() {
//        Long chatId = createTestChat();
//        Link testLink = createTestLink("https://example.com");
//
//        Link addedLink = linkRepository.add(testLink, chatId);
//
//        assertNotNull(addedLink);
//        assertNotNull(addedLink.getId());
//
//        Optional<Link> foundLink = linkRepository.findByUrlAndChatId(testLink.getUrl(), chatId);
//        assertTrue(foundLink.isPresent());
//        assertEquals(testLink.getUrl(), foundLink.get().getUrl());
//        assertEquals("testCreator", foundLink.get().getCreatedBy());
//    }
//
//    @Test
//    @Transactional
//    public void removeLinkTest() {
//        Long chatId = createTestChat();
//        Link testLink = createTestLink("https://example-to-remove.com");
//        Link addedLink = linkRepository.add(testLink, chatId);
//
//        assertNotNull(addedLink.getId());
//
//        linkRepository.remove(addedLink.getId(), chatId);
//
//        assertFalse(linkRepository.existsByUrlAndChatId(testLink.getUrl(), chatId));
//    }
//
//}
