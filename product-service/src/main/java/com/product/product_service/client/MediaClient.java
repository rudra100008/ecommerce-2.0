package com.product.product_service.client;

import com.product.product_service.DTOs.Media.MediaDeleteRequest;
import com.product.product_service.DTOs.Media.MediaUploadResponse;
import com.shared_library.Config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FeignClient(name = "media-service",configuration = FeignConfig.class)
public interface MediaClient {

    @PostMapping(
            value = "/api/media/upload",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    MediaUploadResponse uploadImage(
            @RequestPart("file") MultipartFile file,
            @RequestParam("folder")String folder
    );


    @PostMapping(
            value = "/api/media/upload/multiple",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    List<MediaUploadResponse> uploadMultipleImages(
            @RequestPart("files") List<MultipartFile> files,
            @RequestParam("folder") String folder);

    @DeleteMapping("/api/media/delete")
    void deleteImage(@RequestBody MediaDeleteRequest request);
}
