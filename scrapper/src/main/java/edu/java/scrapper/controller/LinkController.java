package edu.java.scrapper.controller;

import edu.java.model.dto.request.AddLinkRequest;
import edu.java.model.dto.request.RemoveLinkRequest;
import edu.java.scrapper.service.LinkService;
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
        return linkService.getAllLinks(tgChatId);
    }

    @PostMapping
    public ResponseEntity<?> addLink(
        @RequestHeader(TG_CHAT_ID_HEADER) Long tgChatId,
        @RequestBody AddLinkRequest request
    ) {
        return linkService.addLink(tgChatId, request);
    }

    @DeleteMapping
    public ResponseEntity<?> removeLink(
        @RequestHeader(TG_CHAT_ID_HEADER) Long tgChatId, @RequestBody RemoveLinkRequest request
    ) {
        return linkService.removeLink(tgChatId, request);
    }
}
