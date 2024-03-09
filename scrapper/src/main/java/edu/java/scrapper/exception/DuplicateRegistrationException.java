package edu.java.scrapper.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class DuplicateRegistrationException extends ResponseStatusException {
    public DuplicateRegistrationException(String username) {
        super(HttpStatus.BAD_REQUEST, "User '%s' is already registered".formatted(username));
    }
}
