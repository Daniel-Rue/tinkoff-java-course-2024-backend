package edu.java.scrapper.domain.jooq;

import edu.java.scrapper.domain.entity.Link;
import edu.java.scrapper.domain.jooq.codegen.tables.records.LinkRecord;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import static edu.java.scrapper.domain.jooq.codegen.Tables.CHAT_LINK;
import static edu.java.scrapper.domain.jooq.codegen.Tables.LINK;

@Repository
public class JooqLinkRepository {

    private final DSLContext dsl;

    public JooqLinkRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Transactional
    public Link add(Link link, Long chatId) {
        Long linkId = ensureLinkExistsAndGetId(link);
        linkChatIfNotLinked(linkId, chatId);
        return new Link(linkId, link.getUrl(), link.getLastCheckTime(), link.getCreatedAt(), link.getCreatedBy());
    }

    @Transactional
    public void remove(Long linkId, Long chatId) {
        removeChatLink(linkId, chatId);
        removeLinkIfOrphaned(linkId);
    }

    @Transactional
    public List<Link> findAll() {
        return dsl.selectFrom(LINK)
            .fetch()
            .map(this::linkRecordToLink);
    }

    @Transactional
    public Optional<Link> findByUrlAndChatId(String url, Long chatId) {
        return dsl.select(LINK.fields())
            .from(LINK)
            .join(CHAT_LINK).on(LINK.ID.eq(CHAT_LINK.LINK_ID))
            .where(LINK.URL.eq(url).and(CHAT_LINK.CHAT_ID.eq(chatId)))
            .fetchOptional()
            .map(record -> record.into(LINK).into(Link.class));
    }

    @Transactional
    public List<Link> findAllByChatId(Long chatId) {
        return dsl.select(LINK.fields())
            .from(LINK)
            .join(CHAT_LINK).on(LINK.ID.eq(CHAT_LINK.LINK_ID))
            .where(CHAT_LINK.CHAT_ID.eq(chatId))
            .fetch()
            .map(record -> record.into(LINK).into(Link.class));
    }

    @Transactional
    public List<Link> findLinksToCheck(OffsetDateTime thresholdTime) {
        return dsl.selectFrom(LINK)
            .where(LINK.LAST_CHECK_TIME.lessThan(thresholdTime))
            .fetch()
            .map(this::linkRecordToLink);
    }

    @Transactional
    public List<Long> findSubscribedChats(Long linkId) {
        return dsl.select(CHAT_LINK.CHAT_ID)
            .from(CHAT_LINK)
            .where(CHAT_LINK.LINK_ID.eq(linkId))
            .fetchInto(Long.class);
    }

    @Transactional
    public void updateLastCheckTime(Long linkId, OffsetDateTime lastCheckTime) {
        dsl.update(LINK)
            .set(LINK.LAST_CHECK_TIME, lastCheckTime)
            .where(LINK.ID.eq(linkId))
            .execute();
    }

    @Transactional
    public boolean existsByUrlAndChatId(String url, Long chatId) {
        return dsl.fetchExists(
            dsl.select(LINK.ID)
                .from(LINK)
                .join(CHAT_LINK).on(LINK.ID.eq(CHAT_LINK.LINK_ID))
                .where(LINK.URL.eq(url).and(CHAT_LINK.CHAT_ID.eq(chatId))
                )
        );
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

    private Long ensureLinkExistsAndGetId(Link link) {
        Record1<Long> existingLink = dsl.select(LINK.ID)
            .from(LINK)
            .where(LINK.URL.eq(link.getUrl()))
            .fetchOne();

        if (existingLink != null) {
            return existingLink.value1();
        } else {
            LinkRecord insertedRecord = dsl.insertInto(LINK)
                .set(LINK.URL, link.getUrl())
                .set(LINK.LAST_CHECK_TIME, link.getLastCheckTime())
                .set(LINK.CREATED_AT, link.getCreatedAt())
                .set(LINK.CREATED_BY, link.getCreatedBy())
                .returning(LINK.ID)
                .fetchOne();

            if (insertedRecord == null || insertedRecord.getId() == null) {
                throw new IllegalStateException("Failed to insert the new link.");
            }

            return insertedRecord.getId();
        }
    }

    private void linkChatIfNotLinked(Long linkId, Long chatId) {
        Record1<Integer> existsInChatLink = dsl.selectCount()
            .from(CHAT_LINK)
            .where(CHAT_LINK.LINK_ID.eq(linkId)
                .and(CHAT_LINK.CHAT_ID.eq(chatId)))
            .fetchOne();

        if (existsInChatLink == null || existsInChatLink.value1() == 0) {
            dsl.insertInto(CHAT_LINK)
                .set(CHAT_LINK.CHAT_ID, chatId)
                .set(CHAT_LINK.LINK_ID, linkId)
                .set(CHAT_LINK.SUBSCRIBED_AT, OffsetDateTime.now())
                .execute();
        }
    }

    private void removeChatLink(Long linkId, Long chatId) {
        dsl.deleteFrom(CHAT_LINK)
            .where(CHAT_LINK.LINK_ID.eq(linkId))
            .and(CHAT_LINK.CHAT_ID.eq(chatId))
            .execute();
    }

    private void removeLinkIfOrphaned(Long linkId) {
        boolean hasMoreLinks = dsl.fetchExists(
            dsl.selectFrom(CHAT_LINK)
                .where(CHAT_LINK.LINK_ID.eq(linkId))
        );

        if (!hasMoreLinks) {
            dsl.deleteFrom(LINK)
                .where(LINK.ID.eq(linkId))
                .execute();
        }
    }
}
