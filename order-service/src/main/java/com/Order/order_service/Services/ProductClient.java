package com.Order.order_service.Services;

import com.Order.order_service.DTOs.Product.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service")
public interface ProductClient {

    @GetMapping("/api/product/{id}")
    ProductResponse getById(@PathVariable Long id);
}
