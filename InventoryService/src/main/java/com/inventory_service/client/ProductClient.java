package com.inventory_service.client;

import com.inventory_service.Constants.ApiConstants;
import com.inventory_service.Constants.PageConstant;
import com.inventory_service.DTOs.ProductResponse;
import com.shared_library.Config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "product-service",configuration = FeignConfig.class)
public interface ProductClient {

    @GetMapping(ApiConstants.API_PRODUCT_BY_PRODUCT_ID)
    ProductResponse getProductById(@PathVariable Long productId);


    @GetMapping("/api/product/fetchAll")
    List<ProductResponse> getAllProducts(
            @RequestParam(required = false, defaultValue = PageConstant.PAGE_NUMBER)Integer pageNumber,
            @RequestParam(required = false,defaultValue = PageConstant.PAGE_SIZE)Integer pageSize,
            @RequestParam(required = false,defaultValue = PageConstant.SORT_BY) String sortBy,
            @RequestParam(required = false,defaultValue = PageConstant.SORT_DIR)String sortDir
    );
}
