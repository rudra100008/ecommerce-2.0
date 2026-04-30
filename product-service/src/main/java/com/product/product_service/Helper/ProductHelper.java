package com.product.product_service.Helper;

import com.product.product_service.DTOs.Product.ProductRequest;
import com.product.product_service.Entities.Product;
import com.product.product_service.Exceptions.AlreadyExistException;
import com.product.product_service.Repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProductHelper {
    private final ProductRepository productRepository;

    public Product buildProduct(ProductRequest request,String category){
        return Product.builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .discount(request.discount())
                .sku(generateSku(request,category))
                .active(true)
                .build();
    }
    public synchronized String generateSku(ProductRequest request,String category){
        if(request.sku() != null){
            String manualSku = request.sku().trim().toUpperCase();
            if(this.productRepository.existsBySku(manualSku)){
                throw new AlreadyExistException(String.format("Sku %s alrady exists.",manualSku));
            }
            return manualSku;
        }
        String baseSku = generateBaseSku(request, category);
        Set<String> existingSkus = new HashSet<>(productRepository.findSkuStartingWith(baseSku));

        if (!existingSkus.contains(baseSku)) {
            return baseSku;
        }

        int counter = 1;
        String candidate;
        do {
            candidate = baseSku + "_" + counter;
            counter++;
            if (counter > 999) {
                return baseSku + "_" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
            }
        } while (existingSkus.contains(candidate));

        return candidate;

    }


    private String generateBaseSku(ProductRequest request, String category) {
        if (request.name() == null || category == null) {
            throw new IllegalArgumentException("Product name and category are required for SKU generation");
        }

        String categoryPart = normalize(category);
        String productPart = normalize(request.name());

        if (categoryPart.length() > 20)
            categoryPart = categoryPart.substring(0, 20);
        if (productPart.length() > 30)
            productPart = productPart.substring(0, 30);

        categoryPart = categoryPart.replaceAll("_$", "");
        productPart = productPart.replaceAll("_$", "");

        return categoryPart + "_" + productPart;
    }

    private String normalize(String input) {
        return input
                .trim()
                .toUpperCase()
                .replaceAll("[^A-Z0-9]+", "_")
                .replaceAll("^_|_$", "");
    }
}
