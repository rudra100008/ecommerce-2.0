package com.product.product_service.Controller;

import com.product.product_service.DTOs.Category.CategoryRequest;
import com.product.product_service.DTOs.Product.ProductRequest;
import com.product.product_service.DTOs.Product.ProductResponse;
import com.product.product_service.DTOs.Product.ProductDTO;
import com.product.product_service.Services.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE,MediaType.APPLICATION_JSON_VALUE})

    public ResponseEntity<ProductDTO> create(
            @Valid @RequestPart("product") ProductRequest productRequest,
            @Valid @RequestPart("category") CategoryRequest categoryRequest,
            @RequestPart("images") List<MultipartFile> imageFiles
    )
    {
        ProductDTO productDTO = this.productService.create(productRequest,imageFiles,categoryRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(productDTO);
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

    @GetMapping("/{productId}/details")
    public ProductDTO fetchProductWithInventory(
            @PathVariable Long productId
    ){
        return this.productService.getProductDetails(productId);
    }

    @GetMapping("/fetchAll/details")
    public ResponseEntity<?> fetchAllProducts(

    ){
        List<ProductDTO> productDTOS = this.productService.fetchAllProductsDetail();
        return ResponseEntity.status(HttpStatus.OK).body(productDTOS);
    }



}
