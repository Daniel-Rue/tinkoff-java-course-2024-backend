package edu.java.scrapper.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ChatNotFoundException extends ResponseStatusException {
    public ChatNotFoundException(Long chatId) {
        super(HttpStatus.NOT_FOUND, "Chat with ID %d not found".formatted(chatId));
    }
}
