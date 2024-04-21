package edu.java.scrapper.domain.jpa;

import edu.java.scrapper.domain.entity.TgChat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaTgChatRepository extends JpaRepository<TgChat, Long> {
}
