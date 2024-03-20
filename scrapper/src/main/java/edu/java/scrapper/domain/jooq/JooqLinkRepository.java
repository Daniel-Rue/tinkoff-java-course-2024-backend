package edu.java.scrapper.domain.jooq;

import edu.java.scrapper.domain.entity.Link;
import edu.java.scrapper.domain.jooq.codegen.tables.records.LinkRecord;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import static edu.java.scrapper.domain.jooq.codegen.Tables.CHAT_LINK;
import static edu.java.scrapper.domain.jooq.codegen.Tables.LINK;

@Repository
public class JooqLinkRepository {

    private final DSLContext dsl;

    public JooqLinkRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public Link add(Link link) {
        LinkRecord record = dsl.insertInto(LINK)
            .set(LINK.URL, link.getUrl())
            .set(LINK.CREATED_AT, link.getCreatedAt())
            .set(LINK.LAST_CHECK_TIME, link.getLastCheckTime())
            .set(LINK.CREATED_BY, link.getCreatedBy())
            .returning()
            .fetchOne();
        return record == null ? null : linkRecordToLink(record);
    }

    public void remove(Long linkId) {
        dsl.deleteFrom(LINK)
            .where(LINK.ID.eq(linkId))
            .execute();
    }

    public List<Link> findAll() {
        return dsl.selectFrom(LINK)
            .fetch()
            .map(this::linkRecordToLink);
    }

    public Optional<Link> findByUrlAndChatId(String url, Long chatId) {
        LinkRecord record = dsl.select(LINK.fields())
            .from(LINK)
            .join(CHAT_LINK).on(LINK.ID.eq(CHAT_LINK.LINK_ID))
            .where(LINK.URL.eq(url).and(CHAT_LINK.CHAT_ID.eq(chatId)))
            .fetchOneInto(LinkRecord.class); // Прямое преобразование результата запроса в LinkRecord
        return Optional.ofNullable(record).map(this::linkRecordToLink);
    }

    public List<Link> findLinksToCheck(OffsetDateTime thresholdTime) {
        return dsl.selectFrom(LINK)
            .where(LINK.LAST_CHECK_TIME.lessThan(thresholdTime))
            .fetch()
            .map(this::linkRecordToLink);
    }

    public List<Link> findAllByChatId(Long chatId) {
        return dsl.select(LINK.fields())
            .from(LINK)
            .join(CHAT_LINK).on(LINK.ID.eq(CHAT_LINK.LINK_ID))
            .where(CHAT_LINK.CHAT_ID.eq(chatId))
            .fetchInto(LinkRecord.class) // Получаем список LinkRecord
            .stream()
            .map(this::linkRecordToLink)
            .collect(Collectors.toList());
    }

    public List<Long> findSubscribedChats(Long linkId) {
        return dsl.select(CHAT_LINK.CHAT_ID)
            .from(CHAT_LINK)
            .where(CHAT_LINK.LINK_ID.eq(linkId))
            .fetchInto(Long.class);
    }

    public void updateLastCheckTime(Long linkId, OffsetDateTime lastCheckTime) {
        dsl.update(LINK)
            .set(LINK.LAST_CHECK_TIME, lastCheckTime)
            .where(LINK.ID.eq(linkId))
            .execute();
    }

    public boolean existsById(Long linkId) {
        return dsl.fetchExists(dsl.selectFrom(LINK).where(LINK.ID.eq(linkId)));
    }

    private Link linkRecordToLink(LinkRecord record) {
        return new Link(
            record.getId(),
            record.getUrl(),
            record.getLastCheckTime(),
            record.getCreatedAt(),
            record.getCreatedBy()
        );
    }
}
