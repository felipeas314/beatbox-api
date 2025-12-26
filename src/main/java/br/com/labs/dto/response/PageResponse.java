package br.com.labs.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

@Schema(description = "Paginated response wrapper")
public record PageResponse<T>(
        @Schema(description = "Page content")
        List<T> content,

        @Schema(description = "Current page number (0-indexed)", example = "0")
        int page,

        @Schema(description = "Page size", example = "20")
        int size,

        @Schema(description = "Total number of elements", example = "100")
        long totalElements,

        @Schema(description = "Total number of pages", example = "5")
        int totalPages,

        @Schema(description = "Whether this is the first page")
        boolean first,

        @Schema(description = "Whether this is the last page")
        boolean last
) {
    public static <T, R> PageResponse<R> fromPage(Page<T> page, Function<T, R> mapper) {
        return new PageResponse<>(
                page.getContent().stream().map(mapper).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }

    public static <T> PageResponse<T> fromPage(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }
}
