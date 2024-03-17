package edu.java.scrapper.controller;

import edu.java.model.dto.request.AddLinkRequest;
import edu.java.model.dto.request.RemoveLinkRequest;
import edu.java.model.dto.response.LinkResponse;
import edu.java.scrapper.exception.ChatNotFoundException;
import edu.java.scrapper.exception.DuplicateRegistrationException;
import edu.java.scrapper.exception.LinkAlreadyExistsException;
import edu.java.scrapper.exception.LinkNotFoundException;
import edu.java.scrapper.service.LinkService;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/links")
@RequiredArgsConstructor
public class LinkController {
    private static final String TG_CHAT_ID_HEADER = "Tg-Chat-Id";

    private final LinkService linkService;

    @GetMapping
    public ResponseEntity<?> getAllLinks(@RequestHeader(TG_CHAT_ID_HEADER) Long tgChatId) {
        var links = linkService.listAll(tgChatId);
        return ResponseEntity.ok(links);
    }

    @PostMapping
    public ResponseEntity<?> addLink(
        @RequestHeader(TG_CHAT_ID_HEADER) Long tgChatId,
        @RequestBody AddLinkRequest request
    ) {
        try {
            var addedLink = linkService.add(tgChatId, request.link());
            LinkResponse response = new LinkResponse(addedLink.getId(), URI.create(addedLink.getUrl()));
            return ResponseEntity.ok(response);
        } catch (DuplicateRegistrationException | LinkAlreadyExistsException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (ChatNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping
    public ResponseEntity<?> removeLink(
        @RequestHeader(TG_CHAT_ID_HEADER) Long tgChatId, @RequestBody RemoveLinkRequest request
    ) {
        try {
            var removedLink = linkService.remove(tgChatId, request.link());
            LinkResponse response = new LinkResponse(removedLink.getId(), URI.create(removedLink.getUrl()));
            return ResponseEntity.ok(response);
        } catch (LinkNotFoundException | ChatNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
