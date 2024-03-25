package edu.java.scrapper.domain.jpa;

import edu.java.scrapper.domain.entity.Link;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JpaLinkRepository extends JpaRepository<Link, Long> {

    Optional<Link> findByUrl(String url);

    @Query("SELECT l FROM Link l WHERE l.url = :url AND l.id IN (SELECT cl.link.id FROM ChatLink cl WHERE cl.chat.id = :chatId)")
    Optional<Link> findByUrlAndChatId(@Param("url") String url, @Param("chatId") Long chatId);

    @Query("SELECT l FROM Link l JOIN l.chats c WHERE c.id = :chatId")
    List<Link> findAllByChatId(@Param("chatId") Long chatId);

    @Query("SELECT l FROM Link l WHERE l.lastCheckTime < :thresholdTime")
    List<Link> findLinksToCheck(@Param("thresholdTime") OffsetDateTime thresholdTime);

    @Query("SELECT c.id FROM Chat c JOIN c.links l WHERE l.id = :linkId")
    List<Long> findSubscribedChats(@Param("linkId") Long linkId);

    @Query("SELECT COUNT(l) > 0 FROM Link l JOIN l.chats c WHERE l.url = :url AND c.id = :chatId")
    boolean existsByUrlAndChatId(@Param("url") String url, @Param("chatId") Long chatId);
}
