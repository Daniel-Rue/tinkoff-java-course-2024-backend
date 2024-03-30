package edu.java.scrapper.configuration.domain;

import edu.java.scrapper.domain.jbdc.JdbcLinkRepository;
import edu.java.scrapper.domain.jbdc.JdbcTgChatRepository;
import edu.java.scrapper.service.LinkService;
import edu.java.scrapper.service.TgChatService;
import edu.java.scrapper.service.jdbc.JdbcLinkService;
import edu.java.scrapper.service.jdbc.JdbcTgChatService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jdbc")
public class JdbcAccessConfiguration {

    private  final JdbcLinkRepository linkRepository;

    private final JdbcTgChatRepository chatRepository;

    public JdbcAccessConfiguration(JdbcLinkRepository linkRepository, JdbcTgChatRepository chatRepository) {
        this.linkRepository = linkRepository;
        this.chatRepository = chatRepository;
    }


    @Bean
    public LinkService linkService() {
        return new JdbcLinkService(linkRepository, chatRepository);
    }

    @Bean
    public TgChatService tgChatService() {
        return new JdbcTgChatService(chatRepository);
    }
}
