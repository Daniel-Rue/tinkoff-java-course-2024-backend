package edu.java.scrapper.domain.jbdc;

import edu.java.scrapper.domain.entity.Link;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class JdbcLinkRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT_LINK_SQL =
        "INSERT INTO link (url, last_check_time, created_at, created_by) VALUES (?, ?, ?, ?) RETURNING id";
    private static final String INSERT_CHAT_LINK_SQL =
        "INSERT INTO chat_link (chat_id, link_id, subscribed_at) VALUES (?, ?, ?)";
    private static final String SELECT_ALL_LINKS =
        "SELECT * FROM link";
    private static final String SELECT_LINK_BY_URL_AND_CHAT_ID =
        """
            SELECT l.* FROM link l
            JOIN chat_link cl ON l.id = cl.link_id
            WHERE l.url = ? AND cl.chat_id = ?
            """;
    private static final String SELECT_ALL_BY_CHAT_ID =
        """
            SELECT l.* FROM link l
            JOIN chat_link cl ON l.id = cl.link_id
            WHERE cl.chat_id = ?
            """;
    private static final String DELETE_CHAT_LINK =
        "DELETE FROM chat_link WHERE link_id = ? AND chat_id = ?";
    private static final String COUNT_LINKS_FOR_ID =
        "SELECT COUNT(*) FROM chat_link WHERE link_id = ?";
    private static final String DELETE_LINK_IF_ORPHANED =
        "DELETE FROM link WHERE id = ?";
    private static final String EXISTS_BY_URL_AND_CHAT_ID =
        """
            SELECT COUNT(*)
            FROM chat_link cl
            JOIN link l ON cl.link_id = l.id
            WHERE l.url = ? AND cl.chat_id = ?
            """;

    private static final String FIND_LINKS_TO_CHECK = "SELECT * FROM link WHERE last_check_time < ?";
    private static final String FIND_SUBSCRIBED_CHATS = "SELECT chat_id FROM chat_link WHERE link_id = ?";
    private static final String UPDATE_LAST_CHECK_TIME = "UPDATE link SET last_check_time = ? WHERE id = ?";
    private static final String FIND_BY_URL = "SELECT * FROM link WHERE url = ?";

    public JdbcLinkRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public Link add(Link link, Long chatId) {
        Optional<Link> existingLink = findByUrl(link.getUrl());

        Long linkId;

        if (existingLink.isPresent()) {
            linkId = existingLink.get().getId();
        } else {
            linkId = jdbcTemplate.queryForObject(INSERT_LINK_SQL, new Object[] {
                link.getUrl(),
                link.getLastCheckTime(),
                link.getCreatedAt(),
                link.getCreatedBy()
            }, Long.class);

            if (linkId == null) {
                throw new IllegalStateException("Failed to insert the new link.");
            }
        }

        jdbcTemplate.update(INSERT_CHAT_LINK_SQL, chatId, linkId, OffsetDateTime.now());

        return new Link(linkId, link.getUrl(), link.getLastCheckTime(), link.getCreatedAt(), link.getCreatedBy());
    }

    @Transactional
    public void remove(Long linkId, Long tgChatId) {
        removeChatLink(linkId, tgChatId);
        removeLinkIfOrphaned(linkId);
    }

    @Transactional
    public List<Link> findAll() {
        return jdbcTemplate.query(SELECT_ALL_LINKS, new BeanPropertyRowMapper<>(Link.class));
    }

    @Transactional
    public Optional<Link> findByUrlAndChatId(URI url, Long chatId) {
        List<Link> links =
            jdbcTemplate.query(
                SELECT_LINK_BY_URL_AND_CHAT_ID,
                new Object[] {url.toString(), chatId},
                new BeanPropertyRowMapper<>(Link.class)
            );
        return links.stream().findFirst();
    }

    @Transactional
    public List<Link> findAllByChatId(Long chatId) {
        return jdbcTemplate.query(
            SELECT_ALL_BY_CHAT_ID,
            new Object[] {chatId},
            new BeanPropertyRowMapper<>(Link.class)
        );
    }

    @Transactional
    public List<Link> findLinksToCheck(OffsetDateTime thresholdTime) {
        String sql = FIND_LINKS_TO_CHECK;
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Link.class), thresholdTime);
    }

    @Transactional
    public List<Long> findSubscribedChats(long linkId) {
        String sql = FIND_SUBSCRIBED_CHATS;
        return jdbcTemplate.queryForList(sql, Long.class, linkId);
    }

    @Transactional
    public void updateLastCheckTime(long linkId, OffsetDateTime lastCheckTime) {
        String sql = UPDATE_LAST_CHECK_TIME;
        jdbcTemplate.update(sql, lastCheckTime, linkId);
    }

    private void removeChatLink(Long linkId, Long tgChatId) {
        jdbcTemplate.update(DELETE_CHAT_LINK, linkId, tgChatId);
    }

    private boolean hasMoreLinks(Long linkId) {
        Integer count = jdbcTemplate.queryForObject(COUNT_LINKS_FOR_ID, new Object[] {linkId}, Integer.class);
        return count != null && count > 0;
    }

    private void removeLinkIfOrphaned(Long linkId) {
        if (!hasMoreLinks(linkId)) {
            jdbcTemplate.update(DELETE_LINK_IF_ORPHANED, linkId);
        }
    }

    public boolean existsByUrlAndChatId(URI url, Long chatId) {
        Integer count = jdbcTemplate.queryForObject(
            EXISTS_BY_URL_AND_CHAT_ID,
            new Object[] {url.toString(), chatId},
            Integer.class
        );
        return count != null && count > 0;
    }

    private Optional<Link> findByUrl(String url) {
        List<Link> links = jdbcTemplate.query(
            FIND_BY_URL,
            new Object[] {url},
            new BeanPropertyRowMapper<>(Link.class)
        );
        return links.stream().findFirst();
    }
}
