package com.product.product_service.ServiceImpls;

import com.product.product_service.DTOs.ProductRequest;
import com.product.product_service.DTOs.ProductResponse;
import com.product.product_service.Entities.Product;
import com.product.product_service.Repository.ProductRepository;
import com.product.product_service.Service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;


    @Override
    public ProductResponse add(ProductRequest request) {
        Product product = toProduct(request);
        if(product == null){
            throw new IllegalArgumentException("Product is null");
        }
        Product savedProduct = this.productRepository.save(product);

        return toResponse(savedProduct);
    }

    @Override
    public List<ProductResponse> fetchAll() {
        List<Product> products = this.productRepository.findAll();

        return  products.stream()
                .map(this::toResponse)
                .toList();

    }

    @Override
    public ProductResponse fetchById(Long id) {
        Product product = this.productRepository.findById(id).orElseThrow(
                ()-> new RuntimeException("Product Id not found")
        );

        return this.toResponse(product);
    }

    @Override
    public void delete(Long id) {
        Product product = this.productRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Product Id not found to delete"));
        this.productRepository.delete(product);
    }

    private Product toProduct(ProductRequest request){
        return Product.builder()
                .name(request.productName())
                .description(request.description())
                .price(request.price())
                .build();
    }
    private ProductResponse toResponse(Product product){
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice()
        );
    }
}
