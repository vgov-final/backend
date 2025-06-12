package org.viettel.vgov.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagedResponse<T> {
    
    private List<T> items;
    private PaginationInfo pagination;
    
    public static <T> PagedResponse<T> of(Page<T> page) {
        return new PagedResponse<>(
            page.getContent(),
            new PaginationInfo(
                page.getNumber() + 1, // Convert to 1-based page number
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
            )
        );
    }
    
    public static <T> PagedResponse<T> of(List<T> items, int page, int size, long total) {
        int totalPages = (int) Math.ceil((double) total / size);
        return new PagedResponse<>(
            items,
            new PaginationInfo(page, size, total, totalPages)
        );
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaginationInfo {
        private int page;
        private int size;
        private long total;
        private int totalPages;
    }
}
