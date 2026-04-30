package com.product.product_service.Repository;

import com.product.product_service.Entities.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage,Long> {
    @Query("""
            SELECT i FROM ProductImage i WHERE i.product.id = :productId
            """)
    List<ProductImage> findByProductId(@Param("productId") Long productId);
}
