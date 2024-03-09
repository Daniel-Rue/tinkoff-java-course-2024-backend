package edu.java.scrapper.service;


import edu.java.model.dto.request.AddLinkRequest;
import edu.java.model.dto.request.RemoveLinkRequest;
import edu.java.model.dto.response.LinkResponse;
import edu.java.model.dto.response.ListLinksResponse;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class LinkService {

    public ResponseEntity<ListLinksResponse> getAllLinks(Long tgChatId) {
        return new ResponseEntity<>(new ListLinksResponse(List.of(), 0), HttpStatus.OK);
    }

    public ResponseEntity<LinkResponse> addLink(Long tgChatId, AddLinkRequest request) {
        return new ResponseEntity<>(new LinkResponse(1L, request.link()), HttpStatus.CREATED);
    }

    public ResponseEntity<?> removeLink(Long tgChatId, RemoveLinkRequest request) {
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
