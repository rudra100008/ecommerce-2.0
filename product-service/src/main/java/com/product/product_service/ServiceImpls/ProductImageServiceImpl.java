package com.product.product_service.ServiceImpls;

import com.product.product_service.DTOs.Media.MediaDeleteRequest;
import com.product.product_service.DTOs.Media.MediaUploadResponse;
import com.product.product_service.DTOs.ProductImage.ProductImageDTO;
import com.product.product_service.Entities.Product;
import com.product.product_service.Entities.ProductImage;
import com.product.product_service.Exceptions.ResourceNotFoundException;
import com.product.product_service.Mappers.ProductImageMapper;
import com.product.product_service.Repository.ProductImageRepository;
import com.product.product_service.Services.MediaClient;
import com.product.product_service.Services.ProductImageService;
import com.product.product_service.Services.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductImageServiceImpl implements ProductImageService {
    private final ProductImageRepository productImageRepository;
    private final ProductImageMapper productImageMapper;
    private final MediaClient mediaClient;



    @Override
    @Transactional
    public void delete(Long id) {
        ProductImage productImage = this.productImageRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("product image not found"));

        mediaClient.deleteImage(new MediaDeleteRequest(productImage.getPublicId()));
        this.productImageRepository.delete(productImage);
        log.info("Product image delete successfully:{}",id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductImageDTO> findByProductId(Long productId) {
        List<ProductImage> productImages = this.productImageRepository.findByProductId(productId);
        if(productImages == null || productImages.isEmpty()){
            throw  new ResourceNotFoundException("Product images not found.");
        }

        return this.productImageMapper.toProductImageDTOs(productImages);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, ProductImageDTO> findAllByProductIds(List<Long> productIds) {
        List<ProductImage> productImages = this.productImageRepository.findAllByProductIds(productIds);
        if (productImages == null){
            throw new ResourceNotFoundException("Product images not found.");
        }
        return productImages.stream()
                .collect(Collectors.toMap(
                        i -> i.getProduct().getId(),
                        this.productImageMapper::toProductImageDTO
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long,ProductImageDTO> findPrimaryImages(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()){
            return  Map.of();
        }
        List<ProductImage> productImages = this.productImageRepository.findPrimaryImages(productIds);
        return productImages.stream()
                .filter(ProductImage::isPrimaryImage)
                .collect(Collectors.toMap(
                        i -> i.getProduct().getId(),
                        this.productImageMapper::toProductImageDTO
                ));
    }

    // =========== HELPER METHOD ===========

}
