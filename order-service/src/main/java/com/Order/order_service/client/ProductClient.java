package com.Order.order_service.client;

import com.Order.order_service.DTOs.Product.ProductResponse;

import com.shared_library.Config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(
        name = "product-service",
        configuration = FeignConfig.class
)
public interface ProductClient {

    @GetMapping("/api/product/{id}")
    ProductResponse getById(@PathVariable Long id);

    @GetMapping("/api/product/fetchAll")
    List<ProductResponse> getByIds(@RequestParam("ids")List<Long> productIds);
}
