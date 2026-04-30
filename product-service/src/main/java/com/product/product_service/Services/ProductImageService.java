package com.product.product_service.Services;

import com.product.product_service.DTOs.ProductImage.ProductImageDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProductImageService {

    List<ProductImageDTO> findByProductId(Long productId);
}
