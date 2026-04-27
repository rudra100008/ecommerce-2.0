package com.product.product_service.ServiceImpls;

import com.product.product_service.DTOs.InventoryDTO;
import com.product.product_service.DTOs.ProductRequest;
import com.product.product_service.DTOs.ProductResponse;
import com.product.product_service.DTOs.ProductWithInventory;
import com.product.product_service.Entities.Product;
import com.product.product_service.Exceptions.ResourceNotFoundException;
import com.product.product_service.Mappers.ProductMapper;
import com.product.product_service.Repository.ProductRepository;
import com.product.product_service.Services.InventoryClient;
import com.product.product_service.Services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final InventoryClient inventoryClient;
    private final ProductMapper productMapper;

    @Override
    public ProductResponse add(ProductRequest request) {
        Product product = this.productMapper.toProduct(request);
        if(product == null){
            throw new IllegalArgumentException("Product is null");
        }
        Product savedProduct = this.productRepository.save(product);

        return this.productMapper.toProductResponse(savedProduct);
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
                ()-> new RuntimeException("Product Id not found")
        );

        return this.productMapper.toProductResponse(product);
    }

    @Override
    public void delete(Long id) {
        Product product = this.productRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Product Id not found to delete"));
        this.productRepository.delete(product);
    }

    @Override
    public ProductWithInventory getProductWithInventory(Long productId) {
        Product product = this.productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("Product not found"));
        InventoryDTO inventory = this.inventoryClient.fetchInventoryByProductId(product.getId());

        ProductResponse productResponse = this.productMapper.toProductResponse(product);
        return this.productMapper.toProductWithInventory(productResponse,inventory);

    }

}
