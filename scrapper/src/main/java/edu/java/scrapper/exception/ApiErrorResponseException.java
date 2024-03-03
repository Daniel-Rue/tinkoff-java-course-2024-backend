package edu.java.scrapper.exception;

import edu.java.model.dto.response.ApiErrorResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ApiErrorResponseException extends RuntimeException  {
    private final ApiErrorResponse apiErrorResponse;
}
