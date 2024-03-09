package edu.java.scrapper.handler;

import edu.java.model.dto.response.ApiErrorResponse;
import edu.java.scrapper.exception.ChatNotFoundException;
import edu.java.scrapper.exception.DuplicateRegistrationException;
import edu.java.scrapper.exception.LinkAlreadyExistsException;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    private static final String PROBLEM_OCCURRED_IN_OPERATION = "A problem occurred in the operation";

    @ExceptionHandler(ChatNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorResponse handleChatNotFoundException(ChatNotFoundException ex) {
        return createErrorResponse(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateRegistrationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleDuplicateRegistrationException(DuplicateRegistrationException ex) {
        return createErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(LinkAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiErrorResponse handleLinkAlreadyExistsException(LinkAlreadyExistsException ex) {
        return createErrorResponse(ex, HttpStatus.CONFLICT);
    }

    private ApiErrorResponse createErrorResponse(RuntimeException ex, HttpStatus status) {
        return new ApiErrorResponse(
            PROBLEM_OCCURRED_IN_OPERATION,
            String.valueOf(status.value()),
            ex.getClass().getSimpleName(),
            ex.getMessage(),
            Arrays.stream(ex.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.toList())
        );
    }
}
