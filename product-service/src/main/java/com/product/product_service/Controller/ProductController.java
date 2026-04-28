package com.product.product_service.Controller;

import com.product.product_service.DTOs.Product.ProductRequest;
import com.product.product_service.DTOs.Product.ProductResponse;
import com.product.product_service.DTOs.Product.ProductWithInventory;
import com.product.product_service.Services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping()
    public ProductResponse create(
            @RequestBody ProductRequest productRequest
    )
    {
        return  this.productService.add(productRequest);
    }

    @GetMapping()
    public List<ProductResponse> fetchAll()
    {
        return  this.productService.fetchAll();
    }


    @GetMapping("/{id}")
    public ProductResponse fetchById(
            @PathVariable("id")Long id
    ){
        return  this.productService.fetchById(id);
    }

    @DeleteMapping("/{id}")
    public String deleteById(
            @PathVariable("id")Long id
    ){
        this.productService.delete(id);
        return "Product Deleted successfully";
    }

    @GetMapping("/{productId}/inventory")
    public ProductWithInventory fetchProductWithInventory(
            @PathVariable Long productId
    ){
        return this.productService.getProductWithInventory(productId);
    }



}
