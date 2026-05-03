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
    private final ProductService productService;
    private final MediaClient mediaClient;


    @Override
    public List<ProductImageDTO> uploadImages(Long productId, List<MultipartFile> imageFiles) {
        Product product = this.productService.findEntityById(productId);

        List<ProductImage> productImages = null;
        if(imageFiles != null && !imageFiles.isEmpty()){
            List<MediaUploadResponse> uploaded = uploadImages(imageFiles);
            productImages = uploaded.stream()
                    .map(u ->{
                        ProductImage productImage = ProductImage
                                .builder()
                                .imageUrl(u.imageUrl())
                                .publicId(u.publicId())
                                .folder("products")
                                .primaryImage(false)
                                .build();
                        product.addImage(productImage);
                        return productImage;
                    }).toList();
        }
        return this.productImageMapper.toProductImageDTOs(productImages);
    }

    @Override
    public void delete(Long id) {
        ProductImage productImage = this.productImageRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("product image not found"));

        mediaClient.deleteImage(new MediaDeleteRequest(productImage.getPublicId()));
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
    private List<MediaUploadResponse> uploadImages(List<MultipartFile> imageFiles) {
        return imageFiles.stream()
                .map(file -> mediaClient.uploadImage(file, "products"))
                .toList();
    }
}
