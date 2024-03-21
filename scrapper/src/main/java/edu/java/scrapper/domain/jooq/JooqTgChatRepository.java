package edu.java.scrapper.domain.jooq;

import edu.java.scrapper.domain.jooq.codegen.tables.records.ChatRecord;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.util.Optional;
import static edu.java.scrapper.domain.jooq.codegen.Tables.CHAT;

@Repository
public class JooqTgChatRepository {

    private final DSLContext dsl;

    public JooqTgChatRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Transactional
    public ChatRecord add(long tgChatId, OffsetDateTime createdAt) {
        return dsl.insertInto(CHAT)
            .set(CHAT.CREATED_AT, createdAt)
            .returning()
            .fetchOne();
    }

    @Transactional
    public void remove(long tgChatId) {
        dsl.deleteFrom(CHAT)
            .where(CHAT.ID.eq(tgChatId))
            .execute();
    }

    @Transactional(readOnly = true)
    public boolean existsById(long tgChatId) {
        return dsl.fetchExists(
            dsl.selectFrom(CHAT)
                .where(CHAT.ID.eq(tgChatId))
        );
    }

    @Transactional(readOnly = true)
    public Optional<ChatRecord> findById(long tgChatId) {
        ChatRecord record = dsl.selectFrom(CHAT)
            .where(CHAT.ID.eq(tgChatId))
            .fetchOne();
        return Optional.ofNullable(record);
    }
}
