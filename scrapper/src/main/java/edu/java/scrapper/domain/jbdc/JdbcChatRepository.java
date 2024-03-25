package edu.java.scrapper.domain.jbdc;

import edu.java.scrapper.domain.entity.Chat;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class JdbcChatRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT_CHAT = "INSERT INTO chat (id, created_at) VALUES (?, ?)";
    private static final String DELETE_CHAT = "DELETE FROM chat WHERE id = ?";
    private static final String SELECT_ALL_CHATS = "SELECT * FROM chat";
    private static final String COUNT_CHAT_BY_ID = "SELECT COUNT(*) FROM chat WHERE id = ?";
    private static final String SELECT_CHAT_BY_ID = "SELECT * FROM chat WHERE id = ?";

    public JdbcChatRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public Chat add(Chat chat) {
        jdbcTemplate.update(
            INSERT_CHAT,
            chat.getId(),
            chat.getCreatedAt()
        );
        return chat;
    }

    @Transactional
    public void remove(Chat chat) {
        jdbcTemplate.update(DELETE_CHAT, chat.getId());
    }

    @Transactional(readOnly = true)
    public List<Chat> findAll() {
        return jdbcTemplate.query(SELECT_ALL_CHATS, new BeanPropertyRowMapper<>(Chat.class));
    }

    public boolean existsById(Long chatId) {
        Integer count = jdbcTemplate.queryForObject(COUNT_CHAT_BY_ID, new Object[] {chatId}, Integer.class);
        return count != null && count > 0;
    }

    public Optional<Chat> findById(long tgChatId) {
        List<Chat> chatList =
            jdbcTemplate.query(SELECT_CHAT_BY_ID, new BeanPropertyRowMapper<>(Chat.class), tgChatId);
        if (chatList.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(chatList.get(0));
    }
}
