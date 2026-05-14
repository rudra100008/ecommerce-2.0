package com.Order.orderservice.fallback;

import com.Order.orderservice.DTOs.ReservationDTO.ReservationRequest;
import com.Order.orderservice.DTOs.ReservationDTO.ReservationResponse;
import com.Order.orderservice.client.InventoryClient;
import com.shared_library.Exceptions.BusinessInvalidException;
import com.shared_library.Exceptions.ServiceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class InventoryClientFallbackFactory implements FallbackFactory<InventoryClient> {
    @Override
    public InventoryClient create(Throwable cause) {
        return new InventoryClient() {
            @Override
            public ReservationResponse createReservation(ReservationRequest request) {
                log.error("createReservation failed. Cause: {}", cause.getMessage());
                throw extractException(cause);
            }

            @Override
            public ReservationResponse updateReservationQuantity(ReservationRequest request) {
                log.error("updateReservationQuantity failed. Cause: {}", cause.getMessage());
                throw extractException(cause);
            }
            @Override
            public void deleteReservation( Long productId) {
                // silent — reservation expires automatically
                log.warn("deleteReservation failed for user, productId: {}. " +
                        "Will expire automatically. Cause: {}",productId, cause.getMessage());
            }

            @Override
            public List<ReservationResponse> validateActiveReservation(List<Long> productIds) {
                log.error("validateActiveReservation failed. Cause: {}", cause.getMessage());
                throw extractException(cause);
            }

            @Override
            public void convertReservations(List<Long> productIds) {
                log.error("convertReservations failed for  productIds: {}. Cause: {}",
                         productIds, cause.getMessage());
                throw extractException(cause);
            }

            @Override
            public void releaseAllReservation( List<Long> productIds) {
                // silent — reservations expire automatically
                log.warn("releaseAllReservation failed for user. " +
                        "Will expire automatically. Cause: {}", cause.getMessage());
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
