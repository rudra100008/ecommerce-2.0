package com.Order.orderservice.fallback;

import com.Order.orderservice.DTOs.Product.ProductResponse;
import com.Order.orderservice.client.ProductClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class ProductClientFallback implements ProductClient {
    @Override
    public ProductResponse getById(Long id) {
        log.error("Fallback: Product service unavailable. Cannot fetch products: {}", id);
        throw new RuntimeException("Product service is unavailable. Please try again later.");
    }

    @Override
    public List<ProductResponse> getByIds(List<Long> productIds) {
        log.error("Fallback: Product service unavailable. Cannot fetch products: {}", productIds);
        throw new RuntimeException("Product service is unavailable. Please try again later.");
    }
}
