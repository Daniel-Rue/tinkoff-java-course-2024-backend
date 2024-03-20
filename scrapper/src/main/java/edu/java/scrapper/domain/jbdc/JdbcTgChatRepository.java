package edu.java.scrapper.domain.jbdc;

import edu.java.scrapper.domain.entity.TgChat;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class JdbcTgChatRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT_CHAT = "INSERT INTO chat (created_at) VALUES (?) RETURNING id";
    private static final String DELETE_CHAT = "DELETE FROM chat WHERE id = ?";
    private static final String SELECT_ALL_CHATS = "SELECT * FROM chat";
    private static final String COUNT_CHAT_BY_ID = "SELECT COUNT(*) FROM chat WHERE id = ?";
    private static final String SELECT_CHAT_BY_ID = "SELECT * FROM chat WHERE id = ?";

    public JdbcTgChatRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public TgChat add(TgChat chat) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
            connection -> {
                PreparedStatement ps = connection.prepareStatement(INSERT_CHAT, new String[] {"id"});
                ps.setObject(1, chat.getCreatedAt());
                return ps;
            },
            keyHolder
        );
        Long newId = keyHolder.getKey().longValue();
        return new TgChat(newId, chat.getCreatedAt());
    }

    @Transactional
    public void remove(TgChat chat) {
        jdbcTemplate.update(DELETE_CHAT, chat.getId());
    }

    @Transactional(readOnly = true)
    public List<TgChat> findAll() {
        return jdbcTemplate.query(SELECT_ALL_CHATS, new BeanPropertyRowMapper<>(TgChat.class));
    }

    public boolean existsById(Long chatId) {
        Integer count = jdbcTemplate.queryForObject(COUNT_CHAT_BY_ID, new Object[] {chatId}, Integer.class);
        return count != null && count > 0;
    }

    public Optional<TgChat> findById(long tgChatId) {
        List<TgChat> chatList =
            jdbcTemplate.query(SELECT_CHAT_BY_ID, new BeanPropertyRowMapper<>(TgChat.class), tgChatId);
        if (chatList.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(chatList.get(0));
    }
}
