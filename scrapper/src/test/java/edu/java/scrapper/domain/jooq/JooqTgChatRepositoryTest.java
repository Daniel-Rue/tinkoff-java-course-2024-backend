//package edu.java.scrapper.domain.jooq;
//
//import edu.java.scrapper.IntegrationEnvironment;
//import edu.java.scrapper.domain.jooq.codegen.tables.records.ChatRecord;
//import org.jooq.DSLContext;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.annotation.Rollback;
//import org.springframework.transaction.annotation.Transactional;
//import org.testcontainers.junit.jupiter.Testcontainers;
//import java.time.OffsetDateTime;
//import java.util.Optional;
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//@Testcontainers
//public class JooqTgChatRepositoryTest {
//
//    @Autowired
//    private DSLContext dsl;
//
//    private JooqTgChatRepository chatRepository;
//
//
//
//    @BeforeEach
//    public void setUp() {
//        chatRepository = new JooqTgChatRepository(dsl);
//    }
//
//    @Test
//    @Transactional
//    public void addAndFindByIdTest() {
//        OffsetDateTime createdAt = OffsetDateTime.now();
//
//        ChatRecord addedChat = chatRepository.add(1, createdAt);
//        assertNotNull(addedChat);
//        assertNotNull(addedChat.getId());
//
//        Optional<ChatRecord> foundChat = chatRepository.findById(addedChat.getId());
//        assertTrue(foundChat.isPresent());
//        assertEquals(addedChat.getId(), foundChat.get().getId());
//    }
//
//    @Test
//    @Transactional
//    public void removeTest() {
//        OffsetDateTime createdAt = OffsetDateTime.now();
//
//        ChatRecord addedChat = chatRepository.add(1, createdAt);
//        long addedChatId = addedChat.getId();
//        chatRepository.remove(addedChatId);
//        assertFalse(chatRepository.existsById(addedChatId));
//    }
//}
