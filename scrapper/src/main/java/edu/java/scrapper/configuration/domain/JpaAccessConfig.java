package edu.java.scrapper.configuration.domain;

import edu.java.scrapper.domain.jpa.JpaLinkRepository;
import edu.java.scrapper.domain.jpa.JpaTgChatRepository;
import edu.java.scrapper.service.LinkService;
import edu.java.scrapper.service.TgChatService;
import edu.java.scrapper.service.jpa.JpaLinkService;
import edu.java.scrapper.service.jpa.JpaTgChatService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jpa")
public class JpaAccessConfig {

    private final JpaLinkRepository linkRepository;

    private final JpaTgChatRepository chatRepository;

    public JpaAccessConfig(JpaLinkRepository linkRepository, JpaTgChatRepository chatRepository) {
        this.linkRepository = linkRepository;
        this.chatRepository = chatRepository;
    }

    @Bean
    public LinkService linkService() {
        return new JpaLinkService(linkRepository, chatRepository);
    }

    @Bean
    public TgChatService tgChatService() {
        return new JpaTgChatService(chatRepository);
    }
}
