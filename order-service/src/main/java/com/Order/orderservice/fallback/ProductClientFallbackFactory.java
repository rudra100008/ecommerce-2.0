package com.Order.orderservice.fallback;

import com.Order.orderservice.DTOs.Product.ProductResponse;
import com.Order.orderservice.client.ProductClient;
import com.shared_library.Exceptions.BusinessInvalidException;
import com.shared_library.Exceptions.ServiceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class ProductClientFallbackFactory implements FallbackFactory<ProductClient> {
    @Override
    public ProductClient create(Throwable cause) {
        return new ProductClient() {
            @Override
            public ProductResponse getById(Long id) {
                log.error("getById failed for productId: {}. Cause: {}", id, cause.getMessage());
                throw extractException(cause);
            }

            @Override
            public List<ProductResponse> getByIds(List<Long> productIds) {
                log.error("getByIds failed. Cause: {}", cause.getMessage());
                throw extractException(cause);
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
