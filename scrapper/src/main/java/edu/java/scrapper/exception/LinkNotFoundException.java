package edu.java.scrapper.exception;

import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class LinkNotFoundException extends ResponseStatusException {

    public LinkNotFoundException(URI url) {
        super(HttpStatus.NOT_FOUND, "Link not found: " + url);
    }
}
