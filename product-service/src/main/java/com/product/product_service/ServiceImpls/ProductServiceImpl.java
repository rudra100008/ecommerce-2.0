package com.product.product_service.ServiceImpls;

import com.product.product_service.DTOs.Category.CategoryDTO;
import com.product.product_service.DTOs.Category.CategoryRequest;
import com.product.product_service.DTOs.Inventory.InventoryDTO;
import com.product.product_service.DTOs.Inventory.InventoryRequest;
import com.product.product_service.DTOs.Media.MediaDeleteRequest;
import com.product.product_service.DTOs.Media.MediaUploadResponse;
import com.product.product_service.DTOs.Product.ProductRequest;
import com.product.product_service.DTOs.Product.ProductResponse;
import com.product.product_service.DTOs.Product.ProductDTO;
import com.product.product_service.DTOs.ProductImage.ProductImageDTO;
import com.product.product_service.Entities.Category;
import com.product.product_service.Entities.Product;
import com.product.product_service.Entities.ProductImage;
import com.product.product_service.Exceptions.BusinessInvalidException;
import com.product.product_service.Exceptions.ImageInvalidException;
import com.product.product_service.Exceptions.ResourceNotFoundException;
import com.product.product_service.Helper.ProductHelper;
import com.product.product_service.Mappers.ProductImageMapper;
import com.product.product_service.Mappers.ProductMapper;
import com.product.product_service.Validator.ProductValidator;
import com.product.product_service.Repository.ProductRepository;
import com.product.product_service.Services.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final InventoryClient inventoryClient;
    private final MediaClient mediaClient;
    private final ProductMapper productMapper;
    private final ProductHelper productHelper;
    private final ProductValidator productValidator;
    private final CategoryService categoryService;
    private final ProductImageMapper productImageMapper;




    @Override
    @Transactional
    public ProductDTO create(ProductRequest request, List<MultipartFile> imageFiles, CategoryRequest categoryRequest) {
        productValidator.validateProduct(request);
        Category category = this.categoryService.findOrCreate(categoryRequest);

        Product product = productHelper.buildProduct(request,category.getName());

        category.addProduct(product);

        if(imageFiles != null && !imageFiles.isEmpty()){
            List<MediaUploadResponse> uploadedImages = uploadImages(imageFiles);

            uploadedImages.forEach(uploaded ->{
                ProductImage productImage = ProductImage.builder()
                        .imageUrl(uploaded.imageUrl())
                        .publicId(uploaded.publicId())
                        .folder("products")
                        .primaryImage(false)
                        .build();
                product.addImage(productImage);
            });
            log.info("Attached {} images to product", uploadedImages.size());
        }
        Product savedProduct = this.productRepository.save(product);
        log.info("Product saved with id: {}", savedProduct.getId());


        InventoryDTO inventoryDTO = createInventory(savedProduct.getId(),request.stockQuantity());
        CategoryDTO categoryDTO = new CategoryDTO(category.getId(),category.getName());
        List<ProductImageDTO> images = productImageMapper.toProductImageDTOs(savedProduct.getImages());

        return this.productMapper.toProductDTO(
                savedProduct,
                inventoryDTO,
                images,
                categoryDTO
        );
    }


    @Override
    public List<ProductResponse> fetchAll() {
        List<Product> products = this.productRepository.findAll();

        return  products.stream()
                .map(this.productMapper::toProductResponse)
                .toList();

    }

    @Override
    public ProductResponse fetchById(Long id) {
        Product product = this.productRepository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException(String.format("Product Id not found: %d",id))
        );

        return this.productMapper.toProductResponse(product);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Product product = this.productRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Product Id not found to delete"));

        product.getImages().forEach(image ->
                mediaClient.deleteImage(new MediaDeleteRequest(image.getPublicId()))
        );
        inventoryClient.deleteByProductId(product.getId());
        this.productRepository.delete(product);
        log.info("Product deleted:{}",id);
    }

    @Override
    @Transactional
    public ProductDTO getProductWithInventory(Long productId) {
        Product product = this.productRepository.findByIdWithCategoryAndImages(productId)
                .orElseThrow(()-> new ResourceNotFoundException("Product not found"));

        InventoryDTO inventory = this.inventoryClient.fetchInventoryByProductId(product.getId());
        CategoryDTO categoryDTO = new CategoryDTO(product.getCategory().getId(),product.getCategory().getName());
        List<ProductImageDTO> images = this.productImageMapper.toProductImageDTOs(product.getImages());


        return this.productMapper.toProductDTO(
                product,
                inventory,
                images,
                categoryDTO
        );

    }


    // ======== Private / Helper Method ==============


    // This already works — no extra dependency needed
    private List<MediaUploadResponse> uploadImages(List<MultipartFile> imageFiles) {
        return imageFiles.stream()
                .map(file -> mediaClient.uploadImage(file, "products"))
                .toList();
    }

    private InventoryDTO createInventory(Long productId, Long stockQuantity)
    {
        try {
            InventoryRequest inventoryRequest = new InventoryRequest(productId, stockQuantity);
            return inventoryClient.createInventory(inventoryRequest);
        }catch (Exception e){
            log.info("Failed to create inventory for productId: {}",productId);
            throw new BusinessInvalidException("Inventory creation failed:{}"+ e.getMessage());
        }
    }
}
