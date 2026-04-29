package com.media_service.Services;

import com.media_service.DTO.MediaUploadResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface MediaService {
    MediaUploadResponse uploadImage(MultipartFile imageFile,String folder);

    void deleteImage(String publicId);

    List<MediaUploadResponse> uploadMultipleImage(List<MultipartFile> imageFiles,String folder);
}
