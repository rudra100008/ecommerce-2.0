package com.inventoryservice.ServiceImpls;



import com.inventoryservice.Constants.PageConstant;
import com.inventoryservice.DTOs.InventoryDTO.InventoryRequest;
import com.inventoryservice.DTOs.InventoryDTO.InventoryResponse;
import com.inventoryservice.DTOs.InventoryDTO.InventoryWithProduct;
import com.inventoryservice.DTOs.ProductResponse;
import com.inventoryservice.Entities.Inventory;
import com.inventoryservice.Mapper.InventoryMapper;
import com.inventoryservice.Repository.InventoryRepository;
import com.inventoryservice.Services.InventoryService;
import com.inventoryservice.client.ProductClient;
import com.shared_library.Exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {
    private final InventoryRepository inventoryRepository;
    private final InventoryMapper inventoryMapper;
    private final ProductClient productClient;


    @Override
    public InventoryResponse create(InventoryRequest request) {
        Inventory inventory = this.inventoryMapper.toInventory(request);

        Inventory saved = this.inventoryRepository.save(inventory);
        return this.inventoryMapper.toInventoryResponse(saved);
    }

    @Override
    public InventoryResponse fetchById(Long id) {
        Inventory inventory = this.inventoryRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Inventory not found by id"));
        return this.inventoryMapper.toInventoryResponse(inventory);
    }

    @Override
    public InventoryResponse fetchByProductId(Long productId) {
        Inventory inventory = this.inventoryRepository.findByProductId(productId)
                .orElseThrow(()-> new ResourceNotFoundException("Inventory not found by productId"));
        return this.inventoryMapper.toInventoryResponse(inventory);
    }

    @Override
    public List<InventoryResponse> fetchAll() {
        List<Inventory>  inventories = this.inventoryRepository.findAll();
        return this.inventoryMapper.toInventoryResponses(inventories);
    }

    @Override
    public void deleteById(Long id) {
        Inventory inventory = this.inventoryRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Inventory not found by id"));
        this.inventoryRepository.delete(inventory);
    }

    @Override
    public void deleteByProductId(Long productId) {
        Inventory inventory = this.inventoryRepository.findByProductId(productId)
                .orElseThrow(()-> new ResourceNotFoundException("Inventory not found by productId"));
        this.inventoryRepository.delete(inventory);
    }

    @Override
    public InventoryWithProduct getInventoryDetails(Long inventoryId) {
        Inventory inventory = this.inventoryRepository.findById(inventoryId)
                .orElseThrow(()-> new ResourceNotFoundException("Inventory not found"));
        ProductResponse productDTO = productClient.getProductById(inventory.getProductId());
        return this.inventoryMapper.toInventoryWithProduct(inventory,productDTO);
    }

    @Override
    public List<InventoryWithProduct> getAll() {
        List<Inventory> inventories =  this.inventoryRepository.findAll();
        List<ProductResponse> productDTO = productClient.getAllProducts(
                0,
                20,
                PageConstant.SORT_BY,
                PageConstant.SORT_DIR
        );

        return this.inventoryMapper.toInventoriesWithProducts(inventories,productDTO);
    }

    @Override
    public long getAvailableStockQuantity(Long productId) {
        Long result = inventoryRepository.getAvailableQuantity(productId, LocalDateTime.now());
        return result != null ? result : 0L;
    }

    @Override
    @Transactional(readOnly = true)
    public Inventory findById(Long id) {
        return this.inventoryRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Inventory not found."));
    }

    @Override
    @Transactional
    public Inventory findByIdWithLock(Long id) {
        return this.inventoryRepository.findByIdWithLock(id)
                .orElseThrow(()-> new ResourceNotFoundException("Inventory not found."));
    }
}
