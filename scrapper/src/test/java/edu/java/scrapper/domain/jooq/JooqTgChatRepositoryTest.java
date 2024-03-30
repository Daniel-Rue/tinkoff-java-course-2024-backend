//package edu.java.scrapper.domain.jooq;
//
//import edu.java.scrapper.IntegrationEnvironment;
//import edu.java.scrapper.domain.jooq.codegen.tables.records.ChatRecord;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import java.time.OffsetDateTime;
//import java.util.Optional;
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//class JooqTgChatRepositoryTest extends IntegrationEnvironment {
//
//    @Autowired
//    private JooqTgChatRepository repository;
//
//    @Test
//    void whenAddChat_thenChatIsAdded() {
//        OffsetDateTime createdAt = OffsetDateTime.now();
//        long tgChatId = 12345L;
//
//        ChatRecord added = repository.add(tgChatId, createdAt);
//
//        assertNotNull(added);
//        assertEquals(tgChatId, added.getId().longValue());
//        assertEquals(createdAt, added.getCreatedAt());
//    }
//
//    @Test
//    void whenRemoveChat_thenChatIsRemoved() {
//        long tgChatId = 12345L;
//        OffsetDateTime createdAt = OffsetDateTime.now();
//        repository.add(tgChatId, createdAt);
//
//        repository.remove(tgChatId);
//
//        assertFalse(repository.existsById(tgChatId));
//    }
//
//    @Test
//    void whenChatExists_thenReturnTrue() {
//        long tgChatId = 12345L;
//        OffsetDateTime createdAt = OffsetDateTime.now();
//        repository.add(tgChatId, createdAt);
//
//        assertTrue(repository.existsById(tgChatId));
//    }
//
//    @Test
//    void whenFindById_thenReturnChat() {
//        long tgChatId = 12345L;
//        OffsetDateTime createdAt = OffsetDateTime.now();
//        repository.add(tgChatId, createdAt);
//
//        Optional<ChatRecord> found = repository.findById(tgChatId);
//
//        assertTrue(found.isPresent());
//        assertEquals(tgChatId, found.get().getId().longValue());
//    }
//}
