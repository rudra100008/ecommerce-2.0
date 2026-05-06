package com.product.product_service.Services;

import com.product.product_service.DTOs.Category.CategoryRequest;
import com.product.product_service.DTOs.PageInfo;
import com.product.product_service.DTOs.Product.*;
import com.product.product_service.Entities.Product;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface ProductService {

     ProductDTO create(ProductRequest request, CategoryRequest categoryRequest);

     PageInfo<ProductWithImageAndCategory> fetchAll(Integer pageNumber, Integer pageSize, String sortBy, String sortDir);

     ProductDTO update(Long id, UpdateProductRequest updateRequest);

     ProductResponse findById(Long id);
     void delete(Long id);

     ProductDTO getProductDetails(Long productId);
     List<ProductResponse> findByIds(List<Long> productIds);

     Product findEntityById(Long id);

}
