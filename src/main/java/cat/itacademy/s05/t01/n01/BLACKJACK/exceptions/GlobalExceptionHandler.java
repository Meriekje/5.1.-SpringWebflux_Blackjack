package cat.itacademy.s05.t01.n01.BLACKJACK.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private Map<String, String> validationErrors;

    public ErrorResponse(HttpStatus status, String message) {
      this.timestamp = LocalDateTime.now();
      this.status = status.value();
      this.error = status.getReasonPhrase();
      this.message = message;
      this.validationErrors = null;
    }
  }

  @ExceptionHandler(GameNotFoundException.class)
  public Mono<ResponseEntity<ErrorResponse>> handleGameNotFound(GameNotFoundException ex) {
    log.error("Game not found: {}", ex.getMessage());
    ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(error));
  }

  @ExceptionHandler(PlayerNotFoundException.class)
  public Mono<ResponseEntity<ErrorResponse>> handlePlayerNotFound(PlayerNotFoundException ex) {
    log.error("Player not found: {}", ex.getMessage());
    ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(error));
  }

  @ExceptionHandler(InvalidGameException.class)
  public Mono<ResponseEntity<ErrorResponse>> handleInvalidGame(InvalidGameException ex) {
    log.error("Invalid game state: {}", ex.getMessage());
    ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error));
  }

  @ExceptionHandler(BlackjackException.class)
  public Mono<ResponseEntity<ErrorResponse>> handleBlackjack(BlackjackException ex) {
    log.error("Blackjack error: {}", ex.getMessage());
    ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error));
  }

  @ExceptionHandler(WebExchangeBindException.class)
  public Mono<ResponseEntity<ErrorResponse>> handleValidation(WebExchangeBindException ex) {
    log.error("Validation error: {}", ex.getMessage());

    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach(error -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });

    ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST, "Invalid input parameters");
    error.setValidationErrors(errors);
    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error));
  }

  @ExceptionHandler(Exception.class)
  public Mono<ResponseEntity<ErrorResponse>> handleGeneric(Exception ex) {
    log.error("Unexpected error: ", ex);
    ErrorResponse error = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error));
  }
}