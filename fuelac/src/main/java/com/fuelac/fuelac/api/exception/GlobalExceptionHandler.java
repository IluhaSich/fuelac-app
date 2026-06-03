package com.fuelac.fuelac.api.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Централизованная обработка ошибок.
 * Возвращает JSON {"message": "..."} с человекочитаемым описанием вместо технических 500.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Ошибки валидации Bean Validation (@Valid на RequestBody).
     * Возвращает 400 со списком нарушенных полей.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fe -> fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "Неверное значение",
                        (first, second) -> first,
                        java.util.LinkedHashMap::new));

        String summary = fieldErrors.entrySet().stream()
                .map(e -> "«" + e.getKey() + "»: " + e.getValue())
                .collect(Collectors.joining("; "));

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", "Ошибка валидации: " + summary);
        body.put("errors", fieldErrors);
        return ResponseEntity.badRequest().body(body);
    }

    /**
     * Бизнес-логические ошибки (IllegalStateException, IllegalArgumentException).
     * Возвращает 400 с сообщением из исключения.
     */
    @ExceptionHandler({IllegalStateException.class, IllegalArgumentException.class})
    public ResponseEntity<Map<String, Object>> handleBusinessError(RuntimeException ex) {
        return ResponseEntity.badRequest()
                .body(Map.of("message", ex.getMessage() != null ? ex.getMessage() : "Ошибка бизнес-логики"));
    }

    /**
     * Ошибки "не найдено" и прочие RuntimeException.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntime(RuntimeException ex) {
        String msg = ex.getMessage();
        if (msg != null && (msg.contains("не найден") || msg.contains("не найдена"))) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.internalServerError()
                .body(Map.of("message", msg != null ? msg : "Внутренняя ошибка сервера"));
    }
}
