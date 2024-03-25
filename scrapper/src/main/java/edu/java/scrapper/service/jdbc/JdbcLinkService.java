package edu.java.scrapper.service.jdbc;

import edu.java.scrapper.domain.entity.Link;
import edu.java.scrapper.domain.jbdc.JdbcLinkRepository;
import edu.java.scrapper.domain.jbdc.JdbcTgChatRepository;
import edu.java.scrapper.exception.ChatNotFoundException;
import edu.java.scrapper.exception.LinkAlreadyExistsException;
import edu.java.scrapper.exception.LinkNotFoundException;
import edu.java.scrapper.service.LinkService;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class JdbcLinkService implements LinkService {

    private final JdbcLinkRepository linkRepository;
    private final JdbcTgChatRepository tgChatRepository;

    @Override
    @Transactional
    public Link add(long tgChatId, URI url) {
        validateChatExists(tgChatId);
        checkLinkDuplication(url, tgChatId);

        return linkRepository.add(
            new Link(null, url.toString(), OffsetDateTime.now(), OffsetDateTime.now(), "System"),
            tgChatId
        );
    }

    @Override
    @Transactional
    public Link remove(long tgChatId, URI url) {
        validateChatExists(tgChatId);

        Link linkToRemove = linkRepository.findByUrlAndChatId(url, tgChatId)
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
    public Collection<Link> findLinksToCheck(OffsetDateTime thresholdTime) {
        return linkRepository.findLinksToCheck(thresholdTime);
    }

    @Override
    public List<Long> findSubscribedChats(long linkId) {
        return linkRepository.findSubscribedChats(linkId);
    }

    @Override
    @Transactional
    public void updateLastCheckTime(long linkId, OffsetDateTime lastCheckTime) {
        linkRepository.updateLastCheckTime(linkId, lastCheckTime);
    }

    private void validateChatExists(long tgChatId) {
        if (!tgChatRepository.existsById(tgChatId)) {
            throw new ChatNotFoundException(tgChatId);
        }
    }

    private void checkLinkDuplication(URI url, long tgChatId) {
        if (linkRepository.existsByUrlAndChatId(url, tgChatId)) {
            throw new LinkAlreadyExistsException(url);
        }
    }
}
