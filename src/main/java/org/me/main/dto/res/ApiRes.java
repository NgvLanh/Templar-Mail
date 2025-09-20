package org.me.main.dto.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.Map;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiRes<T> {
    Boolean success;
    ErrorCodes errorCodes;
    T Data;
    String message;
    Map<String, String> errors;
    Instant timestamp;

    public enum ErrorCodes {
        VALIDATION_ERROR,
        NOT_FOUND,
        UNAUTHORIZED,
        FORBIDDEN,
        INTERNAL_ERROR
    }

    public static <T> ApiRes<T> success(T data) {
        return new ApiRes<>(true, null, data, null, null, Instant.now());
    }

    public static <T> ApiRes<T> success(T data, String message) {
        return new ApiRes<>(true, null, data, message, null, Instant.now());
    }

    public static <T> ApiRes<T> error(String message, ErrorCodes errorCodes) {
        return new ApiRes<>(false, errorCodes, null, message, null, Instant.now());
    }

    public static <T> ApiRes<T> error(String message, ErrorCodes errorCodes, Map<String, String> errors) {
        return new ApiRes<>(false, errorCodes, null, message, errors, Instant.now());
    }
}
