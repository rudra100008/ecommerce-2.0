package com.product.product_service.DTOs;

import java.util.List;

public record PageInfo<T>(
        List<T> content,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean lastPage
) {
}
