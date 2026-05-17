package com.user.user_service.client;

import com.shared_library.Config.FeignConfig;
import com.user.user_service.DTOs.Media.MediaDeleteRequest;
import com.user.user_service.DTOs.Media.MediaUploadResponse;
import com.user.user_service.fallback.MediaClientFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FeignClient(
     name = "media-service",
     configuration =   FeignConfig.class,
     fallbackFactory = MediaClientFallbackFactory.class
)
public interface MediaClient {
    // to save a images in cloud
    @PostMapping(
            value = "/api/media/upload",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    MediaUploadResponse uploadImage(
            @RequestPart("file") MultipartFile file,
            @RequestParam("folder")String folder
    );


    // to save multiple images in cloud
    @PostMapping(
            value = "/api/media/upload/multiple",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    List<MediaUploadResponse> uploadMultipleImages(
            @RequestPart("files") List<MultipartFile> files,
            @RequestParam("folder") String folder);

    // to delete image in cloudinary
    @DeleteMapping("/api/media/delete")
    void deleteImage(@RequestBody MediaDeleteRequest request);
}
