package edu.java.scrapper.exception;

import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class LinkAlreadyExistsException extends ResponseStatusException {
    public LinkAlreadyExistsException(URI url) {
        super(HttpStatus.CONFLICT, "Link '%s' already exists".formatted(url.toString()));
    }
}
