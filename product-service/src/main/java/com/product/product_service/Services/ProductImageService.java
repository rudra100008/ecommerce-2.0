package com.product.product_service.Services;

import com.product.product_service.DTOs.ProductImage.ProductImageDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
public interface ProductImageService {

    void delete(Long id);
    List<ProductImageDTO> findByProductId(Long productId);
    Map<Long,ProductImageDTO> findAllByProductIds(List<Long> productIds);

    Map<Long,ProductImageDTO> findPrimaryImages(List<Long> productIds);
}
