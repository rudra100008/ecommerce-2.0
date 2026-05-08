package com.inventory_service.Fallback;

import com.inventory_service.DTOs.ProductResponse;
import com.inventory_service.client.ProductClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class ProductClientFallback implements ProductClient {
    @Override
    public ProductResponse getProductById(Long productId) {
        log.error("Fallback: Product service unavailable. Cannot fetch products: {}", productId);
        throw new RuntimeException("Product service is unavailable. Please try again later.");
    }

    @Override
    public List<ProductResponse> getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortDir) {
        log.error("Fallback: Product service unavailable. Cannot fetch products");
        throw new RuntimeException("Product service is unavailable. Please try again later.");
    }
}
