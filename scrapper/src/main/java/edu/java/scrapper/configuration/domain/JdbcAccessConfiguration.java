package edu.java.scrapper.configuration.domain;

import edu.java.scrapper.domain.jbdc.JdbcLinkRepository;
import edu.java.scrapper.domain.jbdc.JdbcTgChatRepository;
import edu.java.scrapper.service.LinkService;
import edu.java.scrapper.service.TgChatService;
import edu.java.scrapper.service.jdbc.JdbcLinkService;
import edu.java.scrapper.service.jdbc.JdbcTgChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jdbc")
@RequiredArgsConstructor
public class JdbcAccessConfiguration {

    private final JdbcTemplate jdbcTemplate;

    @Bean
    public LinkService linkService(
        JdbcLinkRepository linkRepository,
        JdbcTgChatRepository chatRepository
    ) {
        return new JdbcLinkService(linkRepository, chatRepository);
    }

    @Bean
    public TgChatService tgChatService(
        JdbcTgChatRepository chatRepository
    ) {
        return new JdbcTgChatService(chatRepository);
    }

    @Bean
    public JdbcLinkRepository linkRepository() {
        return new JdbcLinkRepository(jdbcTemplate);
    }

    @Bean
    public JdbcTgChatRepository tgChatRepository() {
        return new JdbcTgChatRepository(jdbcTemplate);
    }
}
