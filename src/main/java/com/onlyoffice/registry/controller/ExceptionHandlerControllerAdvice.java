package com.onlyoffice.registry.controller;

import com.onlyoffice.registry.InvalidRegistryOperationException;
import com.onlyoffice.registry.dto.GenericResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.TransactionException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
public class ExceptionHandlerControllerAdvice extends ResponseEntityExceptionHandler {
    @ExceptionHandler(InvalidRegistryOperationException.class)
    protected ResponseEntity<Object> ControllerExceptionHandler(InvalidRegistryOperationException e, WebRequest request) {
        GenericResponseDTO body = GenericResponseDTO
                .builder()
                .success(false)
                .message(e.getMessage())
                .build();
        return handleExceptionInternal(e, body, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({Exception.class, TransactionException.class})
    protected ResponseEntity<Object> UntrackedExceptionHandler(Exception e, WebRequest request) {
        log.error(e.getMessage());
        GenericResponseDTO body = GenericResponseDTO
                .builder()
                .success(false)
                .message(e.getMessage())
                .build();
        return handleExceptionInternal(e, body, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return new ResponseEntity<>(GenericResponseDTO
                .builder()
                .success(false)
                .message(ex.getMessage())
                .build(), status);
    }
}
