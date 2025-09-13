package cat.itacademy.Blackjack.error;

import lombok.Builder;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 404: game no encontrado
    @ExceptionHandler(GameNotFound.class)
    public ResponseEntity<ApiError> handleGameNotFound(GameNotFound ex, ServerWebExchange exchange) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), exchange);
    }

    // 404: player no encontrado
    @ExceptionHandler(PlayerNotFound.class)
    public ResponseEntity<ApiError> handlePlayerNotFound(PlayerNotFound ex, ServerWebExchange exchange) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), exchange);
    }

    // 400: acción inválida (o payload inválido de negocio)
    @ExceptionHandler(InvalidAction.class)
    public ResponseEntity<ApiError> handleInvalidAction(InvalidAction ex, ServerWebExchange exchange) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), exchange);
    }

    // 409: intentar jugar una partida ya finalizada
    @ExceptionHandler(GameAlreadyFinished.class)
    public ResponseEntity<ApiError> handleGameAlreadyFinished(GameAlreadyFinished ex, ServerWebExchange exchange) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), exchange);
    }

    // 400: errores de validación @Valid en WebFlux
    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ApiError> handleValidation(WebExchangeBindException ex, ServerWebExchange exchange) {
        String msg = ex.getAllErrors().isEmpty()
                ? "Validation error"
                : ex.getAllErrors().get(0).getDefaultMessage();
        return build(HttpStatus.BAD_REQUEST, msg, exchange);
    }

    // 500: cualquier otra cosa no controlada
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, ServerWebExchange exchange) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error", exchange);
    }

    // -------- helper --------
    private ResponseEntity<ApiError> build(HttpStatus status, String message, ServerWebExchange exchange) {
        ApiError body = ApiError.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(exchange.getRequest().getPath().value())
                .build();
        return ResponseEntity.status(status).body(body);
    }

    @Value
    @Builder
    static class ApiError {
        Instant timestamp;
        int status;
        String error;
        String message;
        String path;
    }
}
