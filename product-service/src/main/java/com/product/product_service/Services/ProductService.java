package com.product.product_service.Services;

import com.product.product_service.DTOs.Category.CategoryRequest;
import com.product.product_service.DTOs.Product.ProductRequest;
import com.product.product_service.DTOs.Product.ProductResponse;
import com.product.product_service.DTOs.Product.ProductDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface ProductService {

     ProductDTO create(ProductRequest request, List<MultipartFile> imageFiles, CategoryRequest categoryRequest);

     List<ProductResponse> fetchAll();

     ProductResponse fetchById(Long id);
     void delete(Long id);

     ProductDTO getProductDetails(Long productId);

     List<ProductDTO> fetchAllProductsDetail();
}
