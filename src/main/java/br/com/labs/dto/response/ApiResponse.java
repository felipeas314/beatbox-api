package br.com.labs.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Standard API response wrapper")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        @Schema(description = "Response data")
        T data,

        @Schema(description = "Response message", example = "Operation completed successfully")
        String message,

        @Schema(description = "Response timestamp")
        LocalDateTime timestamp
) {
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(data, message, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> success(T data) {
        return success(data, "Success");
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(null, message, LocalDateTime.now());
    }
}
