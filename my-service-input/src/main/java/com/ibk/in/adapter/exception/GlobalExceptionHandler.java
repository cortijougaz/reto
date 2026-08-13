package com.ibk.in.adapter.exception;

import com.ibk.core.exception.RecursoNoEncontradoException;
import com.ibk.in.adapter.dto.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Object> handleRecursoNoEncontrado(
            RecursoNoEncontradoException exception
    ) {
        ApiErrorResponse response = new ApiErrorResponse(
                OffsetDateTime.now(ZoneOffset.UTC),
                HttpStatus.NOT_FOUND.value(),
                "Recurso no encontrado",
                exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<Object> handleValidation(
            WebExchangeBindException exception
    ) {
        Map<String, String> validationErrors = new LinkedHashMap<>();
        exception.getFieldErrors().forEach(error ->
                validationErrors.put(error.getField(), error.getDefaultMessage())
        );

        ApiErrorResponse response = new ApiErrorResponse(
                OffsetDateTime.now(ZoneOffset.UTC),
                HttpStatus.BAD_REQUEST.value(),
                validationErrors,
                "La solicitud contiene campos inválidos"
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(response);
    }
}
