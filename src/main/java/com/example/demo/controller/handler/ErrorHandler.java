package com.example.demo.controller.handler;

import com.example.demo.error.DemoException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@ControllerAdvice
public class ErrorHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({ DemoException.class })
    public final Mono<ResponseEntity<Object>> handleDemoException(DemoException ex, ServerWebExchange exchange) {
        return handleErrorResponseException(ex, ex.getHeaders(), ex.getStatusCode(), exchange);
    }
}
