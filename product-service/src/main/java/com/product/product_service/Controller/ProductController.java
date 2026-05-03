package com.product.product_service.Controller;

import com.product.product_service.Constants.PageConstant;
import com.product.product_service.DTOs.Category.CategoryRequest;
import com.product.product_service.DTOs.PageInfo;
import com.product.product_service.DTOs.Product.ProductRequest;
import com.product.product_service.DTOs.Product.ProductResponse;
import com.product.product_service.DTOs.Product.ProductDTO;
import com.product.product_service.DTOs.Product.ProductWithImageAndCategory;
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

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ProductDTO> create(
            @Valid @RequestPart("product") ProductRequest productRequest,
            @Valid @RequestPart("category") CategoryRequest categoryRequest
    )
    {
        ProductDTO productDTO = this.productService.create(productRequest,categoryRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(productDTO);
    }


    // this fetch product with a primary image and categoryD
    @GetMapping("/fetchAll")
    public ResponseEntity<?> fetchAll(
            @RequestParam(required = false, defaultValue = PageConstant.PAGE_NUMBER)Integer pageNumber,
            @RequestParam(required = false,defaultValue = PageConstant.PAGE_SIZE)Integer pageSize,
            @RequestParam(required = false,defaultValue = PageConstant.SORT_BY) String sortBy,
            @RequestParam(required = false,defaultValue = PageConstant.SORT_DIR)String sortDir
    )
    {

        PageInfo<ProductWithImageAndCategory> products = this.productService.fetchAll(
                pageNumber,
                pageSize,
                sortBy,
                sortDir
        );
        return  ResponseEntity.status(HttpStatus.OK).body(products);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> fetchById(
            @PathVariable("id")Long id
    ){
        ProductResponse productResponse =  this.productService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(productResponse);
    }

    @DeleteMapping("/{id}")
    public String deleteById(
            @PathVariable("id")Long id
    ){
        this.productService.delete(id);
        return "Product Deleted successfully";
    }

    // this endpoints give product in details(inventory,images and category)
    @GetMapping("/{productId}/details")
    public ProductDTO fetchProductDetails(
            @PathVariable Long productId
    ){
        return this.productService.getProductDetails(productId);
    }





}
