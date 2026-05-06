package com.product.product_service.ServiceImpls;

import com.product.product_service.Constants.PageConstant;
import com.product.product_service.DTOs.Category.CategoryDTO;
import com.product.product_service.DTOs.Category.CategoryRequest;
import com.product.product_service.DTOs.Inventory.InventoryDTO;
import com.product.product_service.DTOs.Inventory.InventoryRequest;
import com.product.product_service.DTOs.Media.MediaDeleteRequest;
import com.product.product_service.DTOs.Media.MediaUploadResponse;
import com.product.product_service.DTOs.PageInfo;
import com.product.product_service.DTOs.Product.*;
import com.product.product_service.DTOs.ProductImage.ProductImageDTO;
import com.product.product_service.Entities.Category;
import com.product.product_service.Entities.Product;
import com.product.product_service.Entities.ProductImage;

import com.product.product_service.Helper.ProductHelper;
import com.product.product_service.Mappers.ProductImageMapper;
import com.product.product_service.Mappers.ProductMapper;
import com.product.product_service.Repository.ProductRepository;
import com.product.product_service.Services.*;
import com.product.product_service.client.InventoryClient;
import com.product.product_service.client.MediaClient;
import com.shared_library.Exceptions.BusinessInvalidException;
import com.shared_library.Exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final InventoryClient inventoryClient;
    private final MediaClient mediaClient;
    private final ProductMapper productMapper;
    private final ProductHelper productHelper;
    private final CategoryService categoryService;
    private final ProductImageMapper productImageMapper;
    private final ProductImageService productImageService;


    private static final List<String> ALLOWED_SORT_FIELDS = List.of("createdAt", "updatedAt", "productName", "price");


    @Override
    @Transactional
    public ProductDTO create(ProductRequest request,CategoryRequest categoryRequest) {
        List<String> uploadedPublicIds = new ArrayList<>();

        try {
            Category category = categoryService.findOrCreate(categoryRequest);
            Product product = productHelper.buildProduct(request, category.getName());
            category.addProduct(product);

            if (request.images() != null && !request.images().isEmpty()) {
                request.images().forEach(img -> {
                    uploadedPublicIds.add(img.publicId());
                    ProductImage productImage = ProductImage.builder()
                            .imageUrl(img.imageUrl())
                            .publicId(img.publicId())
                            .primaryImage(img.primaryImage())
                            .folder("products")
                            .build();
                    product.addImage(productImage);
                });
            }

            Product saved = productRepository.save(product);
            InventoryDTO inventoryDTO = createInventory(saved.getId(), request.stockQuantity());
            CategoryDTO categoryDTO = new CategoryDTO(category.getId(), category.getName());
            List<ProductImageDTO> images = productImageMapper.toProductImageDTOs(saved.getImages());

            return productMapper.toProductDTO(saved, inventoryDTO, images, categoryDTO);

        } catch (Exception e) {
            // Rollback Cloudinary uploads
            if (!uploadedPublicIds.isEmpty()) {
                uploadedPublicIds.forEach(publicId ->
                        mediaClient.deleteImage(new MediaDeleteRequest(publicId))
                );
                log.warn("Rolled back {} images from Cloudinary", uploadedPublicIds.size());
            }
            throw e;
        }
    }

    @Override
    @Transactional
    public PageInfo<ProductWithImageAndCategory> fetchAll(Integer pageNumber, Integer pageSize, String sortBy, String sortDir) {
        String validateSortBy = ALLOWED_SORT_FIELDS.contains(sortBy) ? sortBy : PageConstant.SORT_BY;
        Sort sort = sortDir.equalsIgnoreCase(PageConstant.SORT_DIR) ?  Sort.by(validateSortBy).descending()
                : Sort.by(validateSortBy).ascending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Page<Product> productPage = this.productRepository.findAllWithCategory(pageable);

        List<Long> productIds =  productPage.getContent()
                .stream()
                .map(Product::getId)
                .toList();

        Map<Long,ProductImageDTO> imageDTOMap = this.productImageService.findPrimaryImages(productIds);

        List<ProductWithImageAndCategory> products = productPage.getContent()
                .stream()
                .map(p -> productMapper.toProductWithImagesAndCategory(
                        p,
                        imageDTOMap.get(p.getId()),
                        new CategoryDTO(p.getCategory().getId(),p.getCategory().getName())
                ))
                .toList();
        return new PageInfo<>(
                products,
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.isLast()
        );
    }

    @Override
    @Transactional
    public ProductDTO update(Long id, UpdateProductRequest updateRequest) {
        Product product = this.productRepository.findByIdWithCategoryAndImages(id)
                .orElseThrow(()-> new ResourceNotFoundException("Product not found"));


        this.productHelper.applyUpdate(updateRequest,product);

        Product saved  = this.productRepository.save(product);

        CategoryDTO categoryDTO = new CategoryDTO(
                saved.getCategory().getId(),
                saved.getCategory().getName()
        );
        InventoryDTO inventoryDTO = this.inventoryClient.fetchInventoryByProductId(saved.getId());
        List<ProductImageDTO> imageDTOS = this.productImageMapper.toProductImageDTOs(saved.getImages());


        return this.productMapper.toProductDTO(
                saved,
                inventoryDTO,
                imageDTOS,
                categoryDTO

        );
    }



    @Override
    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
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
    public ProductDTO getProductDetails(Long productId) {
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

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> findByIds(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<Product> products = this.productRepository.findAllById(productIds);
        List<Long> uniqueProductIds = productIds.stream()
                .distinct()
                .toList();

        Set<Long> foundIds = products.stream()
                .map(Product::getId)
                .collect(Collectors.toSet());

        List<Long> missingIds = uniqueProductIds.stream()
                .filter(id -> !foundIds.contains(id))
                .toList();

        if (!missingIds.isEmpty()) {
            String errorMessage = String.format(
                    "Products not found with IDs: %s. (Requested: %d, Found: %d)",
                    missingIds, uniqueProductIds.size(), foundIds.size()
            );
            throw new ResourceNotFoundException(errorMessage);
        }

        return this.productMapper.toProductResponseList(products);
    }

    @Override
    @Transactional(readOnly = true)
    public Product findEntityById(Long id) {
        if(id == null) {
            throw  new IllegalArgumentException("Product id is null.");
        }
        return this.productRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Product not found."));
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
