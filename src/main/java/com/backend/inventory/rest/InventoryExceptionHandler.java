package com.backend.inventory.rest;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class InventoryExceptionHandler {
    @ExceptionHandler(ResponseStatusException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException exc, HttpServletRequest request) {
        HttpStatusCode statusCode = exc.getStatusCode();
        ErrorResponse errorResponse = new ErrorResponse(
                statusCode,
                exc.getReason(),
                request.getRequestURI()
        );
        return ResponseEntity.status(statusCode).body(errorResponse);
    }
}
