package com.product.product_service.fallback;

import com.product.product_service.DTOs.Media.MediaDeleteRequest;
import com.product.product_service.DTOs.Media.MediaUploadResponse;
import com.product.product_service.client.MediaClient;
import com.shared_library.Exceptions.BusinessInvalidException;
import com.shared_library.Exceptions.ImageInvalidException;
import com.shared_library.Exceptions.ServiceNotFoundException;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
@Slf4j
public class MediaClientFallbackFactory implements FallbackFactory<MediaClient> {
    @Override
    public MediaClient create(Throwable cause) {
        return new MediaClient() {
            @Override
            public MediaUploadResponse uploadImage(MultipartFile file, String folder) {
                log.error("Failed to upload user image:{}",file.getOriginalFilename());
                throw extractException(cause);
            }

            @Override
            public List<MediaUploadResponse> uploadMultipleImages(List<MultipartFile> files, String folder) {;
                return List.of();
            }

            @Override
            public void deleteImage(MediaDeleteRequest request) {
                log.error("Failed to delete user image: {}",request.publicId());
            }
        };
    }

    private RuntimeException extractException(Throwable cause){
        if(cause instanceof BusinessInvalidException e){
            throw e;
        }

        if(cause instanceof ImageInvalidException e){
            throw e;
        }

        if(cause instanceof FeignException e){
            String responseBody = e.contentUTF8();
            if(responseBody != null && !responseBody.isEmpty()){
                throw new BusinessInvalidException(extractMessage(responseBody));
            }
        }

        return new ServiceNotFoundException(
                "Media service is currently unavailable. Please try again."
        );
    }

    private String extractMessage(String responseBody){
        try {
            if (responseBody.contains("\"message\"")) {
                int start = responseBody.indexOf("\"message\"") + 11;
                int end = responseBody.indexOf("\"", start);
                return responseBody.substring(start, end);
            }
        }catch (Exception e){
            log.warn("Could not extract message from response body: {}",responseBody);
        }
        return responseBody;
    }
}
