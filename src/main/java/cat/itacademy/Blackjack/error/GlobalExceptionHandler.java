package cat.itacademy.Blackjack.error;

import lombok.Builder;
import lombok.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import io.r2dbc.spi.R2dbcDataIntegrityViolationException;
import io.r2dbc.spi.R2dbcException;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // --- Excepciones de dominio ---
    @ExceptionHandler(GameNotFound.class)
    public ResponseEntity<ApiError> gameNotFound(GameNotFound ex, ServerWebExchange exchange) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), exchange);
    }

    @ExceptionHandler(PlayerNotFound.class)
    public ResponseEntity<ApiError> playerNotFound(PlayerNotFound ex, ServerWebExchange exchange) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), exchange);
    }

    @ExceptionHandler(InvalidAction.class)
    public ResponseEntity<ApiError> invalidAction(InvalidAction ex, ServerWebExchange exchange) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), exchange);
    }

    @ExceptionHandler(GameAlreadyFinished.class)
    public ResponseEntity<ApiError> gameFinished(GameAlreadyFinished ex, ServerWebExchange exchange) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), exchange);
    }

    // --- Validación @Valid ---
    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ApiError> validation(WebExchangeBindException ex, ServerWebExchange exchange) {
        String msg = ex.getAllErrors().isEmpty()
                ? "Validation error"
                : ex.getAllErrors().get(0).getDefaultMessage();
        return build(HttpStatus.BAD_REQUEST, msg, exchange);
    }

    // --- ResponseStatusException: respeta el código que lanzaste en el service ---
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> responseStatus(ResponseStatusException ex, ServerWebExchange exchange) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        String msg = ex.getReason() != null ? ex.getReason() : status.getReasonPhrase();
        return build(status, msg, exchange);
    }

    // --- Integridad de datos (duplicados) ---
    @ExceptionHandler(R2dbcDataIntegrityViolationException.class)
    public ResponseEntity<ApiError> r2dbcIntegrity(R2dbcDataIntegrityViolationException ex,
                                                   ServerWebExchange exchange) {
        String sqlState = null;

        try {
            var m = ex.getClass().getMethod("getSqlState");
            Object v = m.invoke(ex);
            if (v instanceof String s) sqlState = s;
        } catch (ReflectiveOperationException ignored) { }

        if (sqlState == null && ex.getCause() instanceof R2dbcException re) {
            sqlState = re.getSqlState();
        }

        if ("23000".equals(sqlState)) { // duplicate key
            return build(HttpStatus.CONFLICT, "player name already exists", exchange);
        }
        return build(HttpStatus.BAD_REQUEST, "data integrity violation", exchange);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> springIntegrity(DataIntegrityViolationException ex,
                                                    ServerWebExchange exchange) {
        return build(HttpStatus.CONFLICT, "data integrity violation", exchange);
    }

    // --- Fallbacks sencillos ---
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> badRequest(IllegalArgumentException ex, ServerWebExchange exchange) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), exchange);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError> conflict(IllegalStateException ex, ServerWebExchange exchange) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), exchange);
    }

    // --- Genérico ---
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> generic(Exception ex, ServerWebExchange exchange) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error", exchange);
    }

    // --- Helper común ---
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
