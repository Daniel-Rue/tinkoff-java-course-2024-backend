package edu.java.scrapper.service.jpa;

import edu.java.scrapper.domain.entity.TgChat;
import edu.java.scrapper.domain.entity.Link;
import edu.java.scrapper.domain.jpa.JpaTgChatRepository;
import edu.java.scrapper.domain.jpa.JpaLinkRepository;
import edu.java.scrapper.exception.ChatNotFoundException;
import edu.java.scrapper.exception.LinkAlreadyExistsException;
import edu.java.scrapper.exception.LinkNotFoundException;
import edu.java.scrapper.service.LinkService;
import jakarta.transaction.Transactional;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;


@Primary
@Service
public class JpaLinkService implements LinkService {

    private final JpaLinkRepository linkRepository;
    private final JpaTgChatRepository chatRepository;

    @Autowired
    public JpaLinkService(JpaLinkRepository linkRepository, JpaTgChatRepository chatRepository) {
        this.linkRepository = linkRepository;
        this.chatRepository = chatRepository;
    }

    @Override
    @Transactional
    public Link add(long tgChatId, URI url) {
        validateChatExists(tgChatId);
        checkLinkDuplication(url, tgChatId);

        Link newLink = new Link();
        newLink.setUrl(url.toString());
        newLink.setLastCheckTime(OffsetDateTime.now());
        newLink.setCreatedAt(OffsetDateTime.now());
        newLink.setCreatedBy("System");
        Link savedLink = linkRepository.save(newLink);

        TgChat tgChat = chatRepository.findById(tgChatId)
            .orElseThrow(() -> new ChatNotFoundException(tgChatId));
        savedLink.getTgChats().add(tgChat);
        tgChat.getLinks().add(savedLink);
        chatRepository.save(tgChat);

        return savedLink;
    }

    @Override
    @Transactional
    public Link remove(long tgChatId, URI url) {
        validateChatExists(tgChatId);

        Link linkToRemove = linkRepository.findByUrlAndChatId(url.toString(), tgChatId)
            .orElseThrow(() -> new LinkNotFoundException(url));

        linkRepository.delete(linkToRemove);
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
        Link linkToUpdate = linkRepository.findById(linkId)
            .orElseThrow(() -> new LinkNotFoundException(linkId));
        linkToUpdate.setLastCheckTime(lastCheckTime);
        linkRepository.save(linkToUpdate);
    }

    private void validateChatExists(long tgChatId) {
        if (!chatRepository.existsById(tgChatId)) {
            throw new ChatNotFoundException(tgChatId);
        }
    }

    private void checkLinkDuplication(URI url, long tgChatId) {
        boolean exists = linkRepository.existsByUrlAndChatId(url.toString(), tgChatId);
        if (exists) {
            throw new LinkAlreadyExistsException(url);
        }
    }
}
