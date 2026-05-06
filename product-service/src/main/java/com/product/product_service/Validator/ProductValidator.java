package com.product.product_service.Validator;

import com.product.product_service.DTOs.Product.ProductRequest;
import com.product.product_service.Repository.ProductRepository;
import com.shared_library.Exceptions.AlreadyExistException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductValidator {
    private final ProductRepository productRepository;


    public void validateProduct(ProductRequest request){
        boolean exists = this.productRepository.existsByName(request.name());
        if(exists){
            throw  new AlreadyExistException(String.format("Product %s already exists.",request.name()));
        }
    }

}
