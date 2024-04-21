package edu.java.scrapper.configuration.domain;

import edu.java.scrapper.domain.jooq.JooqLinkRepository;
import edu.java.scrapper.domain.jooq.JooqTgChatRepository;
import edu.java.scrapper.service.LinkService;
import edu.java.scrapper.service.TgChatService;
import edu.java.scrapper.service.jooq.JooqLinkService;
import edu.java.scrapper.service.jooq.JooqTgChatService;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.conf.RenderQuotedNames;
import org.jooq.impl.DefaultConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jooq.DefaultConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jooq")
@RequiredArgsConstructor
public class JooqAccessConfig {

    @Bean
    public LinkService linkService(
        JooqLinkRepository linkRepository,
        JooqTgChatRepository chatRepository
    ) {
        return new JooqLinkService(linkRepository, chatRepository);
    }

    @Bean
    public TgChatService tgChatService(
        JooqTgChatRepository chatRepository
    ) {
        return new JooqTgChatService(chatRepository);
    }

    @Bean
    public JooqLinkRepository linkRepository(DSLContext dslContext) {
        return new JooqLinkRepository(dslContext);
    }

    @Bean
    public JooqTgChatRepository tgChatRepository(DSLContext dslContext) {
        return new JooqTgChatRepository(dslContext);
    }

    @Bean
    public DefaultConfigurationCustomizer postgresJooqCustomizer() {
        return (DefaultConfiguration c) -> c.settings()
            .withRenderSchema(false)
            .withRenderFormatted(true)
            .withRenderQuotedNames(RenderQuotedNames.NEVER);
    }
}
