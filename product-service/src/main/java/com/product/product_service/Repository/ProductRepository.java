package com.product.product_service.Repository;

import com.product.product_service.Entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ProductRepository  extends JpaRepository<Product,Long> {
    Optional<Product> findByName(String name);
    boolean existsByName(String name);
    boolean existsBySku(String sku);

    @Query("SELECT p.sku FROM Product p WHERE p.sku LIKE :prefix%")
    List<String> findSkuStartingWith(@Param("prefix") String prefix);

    @Query("SELECT p FROM Product p JOIN FETCH p.category LEFT JOIN FETCH p.images WHERE p.id = :id")
    Optional<Product> findByIdWithCategoryAndImages(@Param("id")Long id);

    @Query("SELECT p FROM Product p JOIN FETCH p.category WHERE p.id = :id")
    Optional<Product> findByIdWithCategory(@Param("id") Long id);

    @EntityGraph(attributePaths = {"category"})
    @Query("SELECT p FROM Product p WHERE p.active = true")
    Page<Product> findAllWithCategory(Pageable pageable);


    @Query("SELECT p.id FROM Product p WHERE p.id IN :ids")
    Set<Long> findExistingIds(@Param("ids") List<Long> ids);


}
