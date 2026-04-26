package com.product.product_service.Service;

import com.product.product_service.DTOs.ProductRequest;
import com.product.product_service.DTOs.ProductResponse;
import com.product.product_service.Entities.Product;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProductService {

     ProductResponse add(ProductRequest request);

     List<ProductResponse> fetchAll();

     ProductResponse fetchById(Long id);
     void delete(Long id);
}
