package com.product.product_service.ServiceImpls;

import com.product.product_service.DTOs.Category.CategoryDTO;
import com.product.product_service.DTOs.Category.CategoryRequest;
import com.product.product_service.Entities.Category;
import com.product.product_service.Mappers.CategoryMapper;
import com.product.product_service.Repository.CategoryRepository;
import com.product.product_service.Services.CategoryService;
import com.shared_library.Exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;


    @Override
    @Transactional
    public Category findOrCreate(CategoryRequest categoryRequest) {
        return this.categoryRepository.findByNameIgnoreCase(categoryRequest.categoryName())
                .orElseGet(()->{
                    Category category = Category.builder()
                            .name(categoryRequest.categoryName().trim())
                            .build();
                    return this.categoryRepository.save(category);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDTO findById(Long id) {
        Category category = this.categoryRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Category not found."));
        return  this.categoryMapper.toCategoryDTO(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDTO> fetchAll() {
        List<Category> categories = this.categoryRepository.findAll();
        return this.categoryMapper.toCategoryDTOList(categories);
    }


}
