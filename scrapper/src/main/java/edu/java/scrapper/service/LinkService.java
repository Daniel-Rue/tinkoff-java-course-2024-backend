package edu.java.scrapper.service;

import edu.java.scrapper.domain.entity.Link;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;

public interface LinkService {
    Link add(long tgChatId, URI url);

    Link remove(long tgChatId, URI url);

    Collection<Link> listAll(long tgChatId);

    Collection<Link> findLinksToCheck(OffsetDateTime thresholdTime);

    List<Long> findSubscribedChats(long linkId);

    void updateLastCheckTime(long linkId, OffsetDateTime lastCheckTime);
}
