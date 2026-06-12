package com.digital.backend.exceptions;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.StreamSupport;

@ControllerAdvice
@Order(value = Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String INPUT_VALIDATION_FAILED = "Input validation failed.";

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_CONTENT)
    public ResponseEntity<ValidationErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex, WebRequest request) {
        log.debug(
                "Constraint violation exception encountered: {}", ex.getConstraintViolations(), ex);
        List<ValidationError> errors = buildValidationErrors(ex.getConstraintViolations());

        ValidationErrorResponse validationErrorResponse =
                ValidationErrorResponse.validationErrorBuilder()
                        .message(INPUT_VALIDATION_FAILED)
                        .exception(ex.getClass().getName())
                        .path(((ServletWebRequest) request).getRequest().getRequestURI())
                        .errors(errors)
                        .build();

        return ResponseEntity.unprocessableContent().body(validationErrorResponse);
    }


    @ExceptionHandler(EntityAlreadyDeletedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleEntityAlreadyDeleted(
            EntityAlreadyDeletedException ex, WebRequest request
    ) {
        log.debug(
                "Entity already deleted exception encountered: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse =
                ErrorResponse.builder()
                        .timestamp(Instant.now())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .message(ex.getMessage())
                        .exception(ex.getClass().getName())
                        .path(((ServletWebRequest) request).getRequest().getRequestURI())
                        .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(
            EntityNotFoundException ex, WebRequest request) {
        log.debug("Entity not found exception encountered: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse =
                ErrorResponse.builder()
                        .timestamp(Instant.now())
                        .status(HttpStatus.NOT_FOUND.value())
                        .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                        .message(ex.getMessage())
                        .exception(ex.getClass().getName())
                        .path(((ServletWebRequest) request).getRequest().getRequestURI())
                        .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    private List<ValidationError> buildValidationErrors(Set<ConstraintViolation<?>> violations) {
        return violations.stream()
                .map(
                        violation ->
                                ValidationError.builder()
                                        .field(
                                                StreamSupport.stream(
                                                                violation
                                                                        .getPropertyPath()
                                                                        .spliterator(),
                                                                false)
                                                        .reduce((first, second) -> second)
                                                        .orElse(null)
                                                        .toString())
                                        .message(violation.getMessage())
                                        .invalidValue((Serializable) violation.getInvalidValue())
                                        .build())
                .toList();
    }
}
