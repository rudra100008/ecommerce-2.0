package com.inventory_service.Services;

import com.inventory_service.Constants.ApiConstants;
import com.inventory_service.DTOs.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "product-service")
public interface ProductClient {

    @GetMapping(ApiConstants.API_PRODUCT_BY_PRODUCT_ID)
    ProductDTO getProductById(@PathVariable Long productId);


    @GetMapping(ApiConstants.API_PRODUCT)
    List<ProductDTO> getAllProducts();
}
