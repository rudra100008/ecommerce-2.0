package com.product.product_service.ServiceImpls;

import com.product.product_service.DTOs.ProductImage.ProductImageDTO;
import com.product.product_service.Entities.ProductImage;
import com.product.product_service.Exceptions.ResourceNotFoundException;
import com.product.product_service.Mappers.ProductImageMapper;
import com.product.product_service.Mappers.ProductMapper;
import com.product.product_service.Repository.ProductImageRepository;
import com.product.product_service.Repository.ProductRepository;
import com.product.product_service.Services.ProductImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductImageServiceImpl implements ProductImageService {
    private final ProductImageRepository productImageRepository;
    private final ProductImageMapper productImageMapper;


    @Override
    @Transactional(readOnly = true)
    public List<ProductImageDTO> findByProductId(Long productId) {
        List<ProductImage> productImages = this.productImageRepository.findByProductId(productId);
        if(productImages == null || productImages.isEmpty()){
            throw  new ResourceNotFoundException("Product images not found.");
        }

        return this.productImageMapper.toProductImageDTOs(productImages);
    }
}
