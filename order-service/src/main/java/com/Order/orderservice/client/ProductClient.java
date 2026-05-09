package com.Order.orderservice.client;

import com.Order.orderservice.DTOs.Product.ProductResponse;

import com.Order.orderservice.fallback.ProductClientFallbackFactory;
import com.shared_library.Config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(
        name = "product-service",
        configuration = FeignConfig.class,
        fallbackFactory = ProductClientFallbackFactory.class
)
public interface ProductClient {

    @GetMapping("/api/product/{id}")
    ProductResponse getById(@PathVariable Long id);

    @GetMapping("/api/product/fetchAll")
    List<ProductResponse> getByIds(@RequestParam("ids")List<Long> productIds);
}
