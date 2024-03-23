//package edu.java.scrapper.domain.jooq;
//
//import edu.java.scrapper.domain.entity.Link;
//import edu.java.scrapper.domain.jooq.codegen.tables.records.ChatRecord;
//import org.jooq.DSLContext;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//import org.testcontainers.junit.jupiter.Testcontainers;
//import java.time.OffsetDateTime;
//import java.time.temporal.ChronoUnit;
//import java.util.Collection;
//import java.util.List;
//import java.util.Optional;
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
//    private JooqTgChatRepository tgChatRepository;
//
//    @Autowired
//    private DSLContext dsl;
//
//    private Link createTestLink(Long chatId) {
//        return new Link(null, "https://example.com", OffsetDateTime.now(), OffsetDateTime.now(), chatId.toString());
//    }
//    private Link createTestLink2(Long chatId) {
//        return new Link(null, "https://example2.com", OffsetDateTime.now(), OffsetDateTime.now(), chatId.toString());
//    }
//
//    private ChatRecord createTestChat() {
//        OffsetDateTime createdAt = OffsetDateTime.now();
//        return tgChatRepository.add(1, createdAt);
//    }
//
//    @Test
//    @Transactional
//    public void addAndFindByIdTest() {
//        ChatRecord chat = createTestChat();
//        Long chatId = chat.getId();
//        Link testLink = createTestLink(chatId);
//
//        Link addedLink = linkRepository.add(testLink, chatId);
//
//        assertNotNull(addedLink);
//        assertNotNull(addedLink.getId());
//
//        Optional<Link> foundLink = linkRepository.findByUrlAndChatId(testLink.getUrl(), chatId);
//        assertTrue(foundLink.isPresent());
//        assertEquals(testLink.getUrl(), foundLink.get().getUrl());
//        assertEquals(chatId.toString(), foundLink.get().getCreatedBy());
//    }
//
//    @Test
//    @Transactional
//    public void removeLinkTest() {
//        ChatRecord chat = createTestChat();
//        Long chatId = chat.getId();
//        Link testLink = createTestLink(chatId);
//        Link addedLink = linkRepository.add(testLink, chatId);
//
//        assertNotNull(addedLink.getId());
//
//        linkRepository.remove(addedLink.getId(), chatId);
//
//        Optional<Link> foundLink = linkRepository.findByUrlAndChatId(testLink.getUrl(), chatId);
//        assertFalse(foundLink.isPresent());
//    }
//
//    @Test
//    @Transactional
//    public void updateLastCheckTimeTest() {
//        ChatRecord chat = createTestChat();
//        Long chatId = chat.getId();
//        Link testLink = createTestLink(chatId);
//        Link addedLink = linkRepository.add(testLink, chatId);
//
//        OffsetDateTime newLastCheckTime = OffsetDateTime.now().plusDays(1);
//        linkRepository.updateLastCheckTime(addedLink.getId(), newLastCheckTime);
//
//        Optional<Link> updatedLink = linkRepository.findByUrlAndChatId(testLink.getUrl(), chatId);
//        assertTrue(updatedLink.isPresent());
//        assertEquals(newLastCheckTime.truncatedTo(ChronoUnit.SECONDS), updatedLink.get().getLastCheckTime().truncatedTo(ChronoUnit.SECONDS));
//    }
//
//    @Test
//    @Transactional
//    public void findAllByChatIdTest() {
//        ChatRecord chat = createTestChat();
//        Long chatId = chat.getId();
//        Link testLink1 = createTestLink(chatId);
//        Link testLink2 = createTestLink2(chatId);
//
//        linkRepository.add(testLink1, chatId);
//        linkRepository.add(testLink2, chatId);
//
//        Collection<Link> links = linkRepository.findAllByChatId(chatId);
//        assertNotNull(links);
//        assertTrue(links.size() > 1);
//    }
//
//    @Test
//    @Transactional
//    public void findSubscribedChatsTest() {
//        ChatRecord chat = createTestChat();
//        Long chatId = chat.getId();
//        Link testLink = createTestLink(chatId);
//        Link addedLink = linkRepository.add(testLink, chatId);
//
//        List<Long> subscribedChats = linkRepository.findSubscribedChats(addedLink.getId());
//        assertNotNull(subscribedChats);
//        assertTrue(subscribedChats.contains(chatId));
//    }
//}
