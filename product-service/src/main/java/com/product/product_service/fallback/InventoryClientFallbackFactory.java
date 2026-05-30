package com.product.product_service.fallback;

import com.product.product_service.DTOs.Inventory.InventoryDTO;
import com.product.product_service.DTOs.Inventory.InventoryRequest;
import com.product.product_service.client.InventoryClient;
import com.shared_library.Exceptions.BusinessInvalidException;
import com.shared_library.Exceptions.ServiceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class InventoryClientFallbackFactory implements FallbackFactory<InventoryClient> {

    @Override
    public InventoryClient create(Throwable cause) {
        return new InventoryClient() {
            @Override
            public InventoryDTO createInventory(InventoryRequest request) {
                log.error("Failed to create inventory.Cause:{}",cause.getMessage());
                throw extractException(cause);
            }

            @Override
            public InventoryDTO fetchInventoryByProductId(Long productId) {
                log.error("Failed to fetch inventory by product Id.Cause:{}",cause.getMessage());
                throw extractException(cause);
            }

            @Override
            public void deleteByProductId(Long productId) {
                log.error("Failed to delete inventory by productId.Cause: {}",cause.getMessage());
            }

            @Override
            public List<InventoryDTO> findAllByProductIds(List<Long> productIds) {
                log.error("Failed to fetch inventories by productIds. Cause: {}",cause.getMessage());
                return List.of();
            }
        };
    }

    private RuntimeException extractException(Throwable cause){
        if (cause instanceof BusinessInvalidException businessInvalidException){
            return businessInvalidException;
        }

        if(cause instanceof feign.FeignException feignException){
            String responseBody = feignException.contentUTF8();
            if(responseBody != null && !responseBody.isEmpty()){
                return new BusinessInvalidException(extractMessage(responseBody));
            }
        }

        return new ServiceNotFoundException(
                "Inventory service is currently unavailable. Please try again."
        );
    }


    private String extractMessage(String responseBody){

        try{
            if(responseBody.contains("\"message\"")){
                int start = responseBody.indexOf("\"message\"") + 11;
                int end = responseBody.indexOf("\"",start);
                return responseBody.substring(start,end);
            }
        }catch (Exception e){
            log.warn("Could not extract message from response body: {}",responseBody);
        }
        return responseBody;
    }
}
