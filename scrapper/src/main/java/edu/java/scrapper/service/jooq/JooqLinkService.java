package edu.java.scrapper.service.jooq;

import edu.java.scrapper.domain.entity.Link;
import edu.java.scrapper.domain.jooq.JooqLinkRepository;
import edu.java.scrapper.domain.jooq.JooqTgChatRepository;
import edu.java.scrapper.exception.ChatNotFoundException;
import edu.java.scrapper.exception.LinkAlreadyExistsException;
import edu.java.scrapper.exception.LinkNotFoundException;
import edu.java.scrapper.service.LinkService;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Primary
@Service
@RequiredArgsConstructor
public class JooqLinkService implements LinkService {

    private final JooqLinkRepository linkRepository;
    private final JooqTgChatRepository tgChatRepository;

    @Override
    @Transactional
    public Link add(long tgChatId, URI url) {
        validateChatExists(tgChatId);
        Link newLink = new Link(null, url.toString(), OffsetDateTime.now(), OffsetDateTime.now(), "System");
        return linkRepository.add(newLink, tgChatId);
    }

    @Override
    @Transactional
    public Link remove(long tgChatId, URI url) {
        validateChatExists(tgChatId);
        Link linkToRemove = linkRepository.findByUrlAndChatId(url.toString(), tgChatId)
            .orElseThrow(() -> new LinkNotFoundException(url));
        linkRepository.remove(linkToRemove.getId(), tgChatId);
        return linkToRemove;
    }

    @Override
    public Collection<Link> listAll(long tgChatId) {
        validateChatExists(tgChatId);
        return linkRepository.findAllByChatId(tgChatId);
    }

    @Override
    public void updateLastCheckTime(long linkId, OffsetDateTime lastCheckTime) {
        linkRepository.updateLastCheckTime(linkId, lastCheckTime);
    }

    @Override
    public Collection<Link> findLinksToCheck(OffsetDateTime thresholdTime) {
        return linkRepository.findLinksToCheck(thresholdTime);
    }

    @Override
    public List<Long> findSubscribedChats(long linkId) {
        return linkRepository.findSubscribedChats(linkId);
    }

    private void validateChatExists(long tgChatId) {
        if (!tgChatRepository.existsById(tgChatId)) {
            throw new ChatNotFoundException(tgChatId);
        }
    }
}
