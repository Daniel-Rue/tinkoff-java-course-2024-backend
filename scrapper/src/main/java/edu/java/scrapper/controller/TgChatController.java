package edu.java.scrapper.controller;

import edu.java.scrapper.exception.ChatNotFoundException;
import edu.java.scrapper.exception.DuplicateRegistrationException;
import edu.java.scrapper.service.TgChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/tg-chat")
@RequiredArgsConstructor
public class TgChatController {

    private static final String ERROR_MESSAGE_PREFIX = "An error occurred: ";
    private final TgChatService tgChatService;

    @PostMapping("/{id}")
    public ResponseEntity<?> registerChat(@PathVariable Long id) {
        try {
            tgChatService.register(id);
            return ResponseEntity.ok().build();
        } catch (DuplicateRegistrationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ERROR_MESSAGE_PREFIX + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteChat(@PathVariable Long id) {
        try {
            tgChatService.unregister(id);
            return ResponseEntity.ok().build();
        } catch (ChatNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ERROR_MESSAGE_PREFIX + e.getMessage());
        }
    }
}
